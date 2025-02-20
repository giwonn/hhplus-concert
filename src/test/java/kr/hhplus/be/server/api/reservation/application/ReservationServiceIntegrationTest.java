package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.mockapi.application.DataPlatformSendService;
import kr.hhplus.be.server.api.reservation.application.port.in.ConfirmReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationResult;
import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationOutbox;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationOutboxRepository;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationRepository;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationFixture;
import kr.hhplus.be.server.base.BaseIntegrationTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import kr.hhplus.be.server.core.enums.OutboxStatus;
import kr.hhplus.be.server.core.provider.TimeProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Import(FixedClockBean.class)
class ReservationServiceIntegrationTest extends BaseIntegrationTest {

	@Autowired
	ReservationService reservationService;

	@Autowired
	ReservationRepository reservationRepository;

	@Autowired
	ReservationOutboxRepository reservationOutboxRepository;

	@Autowired
	TimeProvider timeProvider;

	@MockitoSpyBean
	DataPlatformSendService dataPlatformSendService;

	@MockitoSpyBean
	ReservationOutboxService reservationOutboxService;

	@Nested
	class 예약_만료_처리 {
		@Test
		void 만료시간이_이미_지난_예약만_처리된다() {
			// given
			final List<Reservation> reservations = List.of(
					ReservationFixture.create( 1L, 1L, 1000L, ReservationStatus.WAITING, timeProvider.now().minusSeconds(Reservation.EXPIRE_SECONDS+1), null),
					ReservationFixture.create( 2L, 2L, 1000L, ReservationStatus.WAITING, timeProvider.now(), null),
					ReservationFixture.create( 3L, 3L, 1000L, ReservationStatus.WAITING, timeProvider.now().plusSeconds(10), null)
			);
			reservationRepository.saveAll(reservations);

			// when
			List<ReservationResult> sut = reservationService.expireReservations();

			// then
			List<Reservation> expiredReservations = reservationRepository.findByStatusWithLock(ReservationStatus.EXPIRED);
			assertAll(() -> {
				assertThat(expiredReservations).hasSize(1);
				assertThat(sut).hasSize(1);
				assertThat(sut.get(0).id()).isEqualTo(expiredReservations.get(0).getId());
				assertThat(sut.get(0).status()).isEqualTo(expiredReservations.get(0).getStatus());
			});
		}
	}

	@Nested
	class 예약_추가 {
		@Test
		void 성공() {
			// given
			CreateReservationDto dto = new CreateReservationDto(1L, 1L, 1000L);

			// when
			ReservationResult sut = reservationService.reserve(dto);

			// then
			assertAll(() -> {
				assertThat(sut.userId()).isEqualTo(dto.userId());
				assertThat(sut.concertSeatId()).isEqualTo(dto.seatId());
				assertThat(sut.amount()).isEqualTo(dto.amount());
				assertThat(sut.status()).isEqualTo(ReservationStatus.WAITING);
				assertThat(sut.expiredAt()).isEqualTo(timeProvider.now().plusSeconds(Reservation.EXPIRE_SECONDS));
			});

		}

	}

	@Nested
	class 예약_조회 {
		@Test
		void 성공() {
			// given
			Reservation reservation = ReservationFixture.create(1L, 1L, 1000L, ReservationStatus.WAITING, timeProvider.now(), null);
			reservationRepository.save(reservation);

			// when
			ReservationResult sut = reservationService.findById(1L);

			// then
			assertAll(() -> {
				assertThat(sut.id()).isEqualTo(reservation.getId());
				assertThat(sut.userId()).isEqualTo(reservation.getUserId());
				assertThat(sut.concertSeatId()).isEqualTo(reservation.getConcertSeatId());
				assertThat(sut.amount()).isEqualTo(reservation.getAmount());
				assertThat(sut.status()).isEqualTo(ReservationStatus.WAITING);
				assertThat(sut.expiredAt()).isEqualTo(reservation.getCreatedAt().plusSeconds(Reservation.EXPIRE_SECONDS));
			});

		}
	}

	@Nested
	class 예약_확정 {
		@Test
		void 성공() {
			// given
			Reservation reservation = ReservationFixture.create(
					1L,
					1L,
					1000L,
					ReservationStatus.WAITING,
					timeProvider.now().minusSeconds(100),
					timeProvider.now()
			);
			reservationRepository.save(reservation);

			ConfirmReservationDto dto = new ConfirmReservationDto(1L, timeProvider.now());

			// when
			ReservationResult sut = reservationService.confirmReservation(dto);

			// then
			assertAll(() -> {
				assertThat(sut.userId()).isEqualTo(1L);
				assertThat(sut.status()).isEqualTo(ReservationStatus.CONFIRMED);
				assertThat(sut.paidAt()).isEqualTo(reservation.getPaidAt());
			});
		}

		@Test
		void 데이터플랫폼_발송_성공() {
			// given
			reservationRepository.save(Reservation.of(1L, 1L, 1000, Instant.now()));
			ConfirmReservationDto dto = new ConfirmReservationDto(1L, timeProvider.now());

			// when
			ReservationResult sut = reservationService.confirmReservation(dto);

			// then
			assertThat(sut.status()).isEqualTo(ReservationStatus.CONFIRMED);
			await()
					.pollInterval(Duration.ofMillis(500))
					.atMost(5, TimeUnit.SECONDS)
					.untilAsserted(() -> {
						verify(dataPlatformSendService, times(1)).sendReservation(any());
						verify(reservationOutboxService, times(1)).updateOutboxPublished(any());
					});

			Optional<ReservationOutbox> outbox = reservationOutboxRepository.findById(1L);
			assertThat(outbox).isNotNull();
			assertThat(outbox.get().getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
		}
	}

}
