package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationResult;
import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationRepository;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;
import kr.hhplus.be.server.api.reservation.domain.entity.TestReservationFactory;
import kr.hhplus.be.server.base.BaseIntegretionTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import kr.hhplus.be.server.common.provider.TimeProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(FixedClockBean.class)
@Transactional
class ReservationServiceIntegretionTest extends BaseIntegretionTest {

	@Autowired
	ReservationService reservationService;

	@Autowired
	ReservationRepository reservationRepository;

	@Autowired
	TimeProvider timeProvider;

	@Nested
	class 예약_만료_처리 {
		@Test
		void 만료시간이_이미_지난_예약만_처리된다() {
			// given
			final List<Reservation> reservations = List.of(
					TestReservationFactory.create( 1L, 1L, 1000L, ReservationStatus.WAITING, timeProvider.now().minusSeconds(10), null),
					TestReservationFactory.create( 2L, 2L, 1000L, ReservationStatus.WAITING, timeProvider.now(), null),
					TestReservationFactory.create( 3L, 3L, 1000L, ReservationStatus.WAITING, timeProvider.now().plusSeconds(10), null)
			);
			reservationRepository.saveAll(reservations);

			// when
			List<ReservationResult> sut = reservationService.expireReservations();

			// then
			List<Reservation> expiredReservations = reservationRepository.findByStatus(ReservationStatus.EXPIRED);
			assertAll(() -> {
				assertThat(expiredReservations).hasSize(1);
				assertThat(sut).hasSize(1);
				assertThat(sut.get(0).id()).isEqualTo(expiredReservations.get(0).getId());
				assertThat(sut.get(0).status()).isEqualTo(expiredReservations.get(0).getStatus());
			});
		}
	}

	@Nested
	class 예약_내역_추가 {
		@Test
		void 성공() {
			// given
			CreateReservationDto dto = new CreateReservationDto(1L, 1L, 1000L);

			// when
			ReservationResult sut = reservationService.reserve(dto);

			// then
			assertAll(() -> {
				assertThat(sut.userId()).isEqualTo(dto.userId());
				assertThat(sut.concertSeatId()).isEqualTo(dto.concertSeatId());
				assertThat(sut.amount()).isEqualTo(dto.amount());
				assertThat(sut.status()).isEqualTo(ReservationStatus.WAITING);
				assertThat(sut.createdAt()).isEqualTo(timeProvider.now());
			});

		}

		@Test
		void 실패_이미_예약된_좌석() {
			// given
			Reservation reservation = TestReservationFactory.create(
					1L,
					1L,
					1000L,
					ReservationStatus.WAITING,
					timeProvider.now(),
					null
			);
			reservationRepository.save(reservation);

			CreateReservationDto dto = new CreateReservationDto(1L, 1L, 1000L);


			// when & then
			assertThatThrownBy(() -> reservationService.reserve(dto))
					.hasMessage(ReservationErrorCode.DUPLICATE_SEAT_RESERVATION.getReason());
		}
	}

	@Nested
	class 예약_결제내역_추가 {
		@Test
		void 성공() {
			// given
			Reservation reservation = TestReservationFactory.create(
					1L,
					1L,
					1000L,
					ReservationStatus.WAITING,
					timeProvider.now(),
					null
			);
			reservationRepository.save(reservation);
			CreateReservationDto dto = new CreateReservationDto(1L, 1L, 1000L);

			// when
			ReservationResult sut = reservationService.addPaymentTime(1);

			// then
			assertAll(() -> {
				assertThat(sut.userId()).isEqualTo(dto.userId());
				assertThat(sut.status()).isEqualTo(ReservationStatus.CONFIRMED);
				assertThat(sut.paidAt()).isEqualTo(timeProvider.now());
			});

		}
	}


}
