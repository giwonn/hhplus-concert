package kr.hhplus.be.server.api.reservation.presentation;

import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeat;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeatFixture;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertSeatRepository;
import kr.hhplus.be.server.api.concert.exception.ConcertErrorCode;
import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.in.ReservationPaymentDto;
import kr.hhplus.be.server.api.reservation.application.port.in.ReserveSeatDto;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationPaymentResult;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationResult;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationFixture;
import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationRepository;
import kr.hhplus.be.server.api.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.api.user.domain.entity.UserFixture;
import kr.hhplus.be.server.api.user.domain.entity.User;
import kr.hhplus.be.server.api.user.domain.entity.UserPointAction;
import kr.hhplus.be.server.api.user.domain.entity.UserPointHistory;
import kr.hhplus.be.server.api.user.domain.repository.UserPointHistoryRepository;
import kr.hhplus.be.server.api.user.domain.repository.UserRepository;
import kr.hhplus.be.server.api.user.domain.exception.UserErrorCode;
import kr.hhplus.be.server.base.BaseIntegrationTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import kr.hhplus.be.server.core.provider.TimeProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(FixedClockBean.class)
class ReservationFacadeIntegrationTest extends BaseIntegrationTest {

	@Autowired
	ReservationFacade reservationFacade;

	@Autowired
	ReservationRepository reservationRepository;

	@Autowired
	ConcertSeatRepository concertSeatRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserPointHistoryRepository userPointHistoryRepository;

	@Autowired
	TimeProvider timeProvider;

	@Nested
	class 예약_만료_처리 {
		@Test
		void 만료시간이_지난_예약은_취소되고_좌석배정은_해제된다() {
			// when
			final List<Reservation> tempReservations = List.of(
					ReservationFixture.create( 1L, 1L, 1000L, ReservationStatus.WAITING, timeProvider.now().minusSeconds(Reservation.EXPIRE_SECONDS+1), null),
					ReservationFixture.create( 2L, 2L, 1000L, ReservationStatus.WAITING, timeProvider.now(), null),
					ReservationFixture.create( 3L, 3L, 1000L, ReservationStatus.WAITING, timeProvider.now().plusSeconds(10), null)
			);
			reservationRepository.saveAll(tempReservations);


			final List<ConcertSeat> tempConcertSeats = List.of(
					ConcertSeatFixture.create( 1L, 1, 1000L, true),
					ConcertSeatFixture.create( 2L, 1, 1000L, true),
					ConcertSeatFixture.create( 3L, 1, 1000L, true)
			);
			concertSeatRepository.saveAll(tempConcertSeats);

			reservationFacade.expireReservations();

			// then
			List<Reservation> expiredReservations = reservationRepository.findAll()
					.stream().sorted(Comparator.comparingLong(Reservation::getId)).toList();
			List<ConcertSeat> concertSeats = concertSeatRepository.findAll()
					.stream().sorted(Comparator.comparingLong(ConcertSeat::getId)).toList();

			assertAll(() -> {
				assertThat(expiredReservations.get(0).getId()).isEqualTo(1L);
				assertThat(expiredReservations.get(0).getStatus()).isEqualTo(ReservationStatus.EXPIRED);
				assertThat(concertSeats.get(0).getId()).isEqualTo(1L);
				assertThat(concertSeats.get(0).isReserved()).isFalse();
			});

			assertAll(() -> {
				assertThat(expiredReservations.get(1).getId()).isEqualTo(2L);
				assertThat(expiredReservations.get(1).getStatus()).isEqualTo(ReservationStatus.WAITING);
				assertThat(concertSeats.get(1).getId()).isEqualTo(2L);
				assertThat(concertSeats.get(1).isReserved()).isTrue();
			});

			assertAll(() -> {
				assertThat(expiredReservations.get(2).getId()).isEqualTo(3L);
				assertThat(expiredReservations.get(2).getStatus()).isEqualTo(ReservationStatus.WAITING);
				assertThat(concertSeats.get(2).getId()).isEqualTo(3L);
				assertThat(concertSeats.get(2).isReserved()).isTrue();
			});
		}
	}

	@Nested
	class 좌석_예약 {

		@Test
		void 성공() {
			// given
			ConcertSeat concertSeat = ConcertSeatFixture.create(3L, 1, 1000L, false);
			concertSeatRepository.save(concertSeat);
			CreateReservationDto dto = new CreateReservationDto(1L, 4L, 1000L);

			// when
			ReservationResult reservation = reservationFacade.reserve(dto);

			// then
			assertThat(reservation.id()).isEqualTo(1L);
			assertThat(reservation.concertSeatId()).isEqualTo(1L);
			assertThat(reservation.userId()).isEqualTo(4L);
			assertThat(reservation.status()).isEqualTo(ReservationStatus.WAITING);
		}

		@Test
		void 실패_이미_좌석이_예약됨() {
			// given
			ConcertSeat concertSeat = ConcertSeatFixture.create(1L, 2, 1000L, true);
			concertSeatRepository.save(concertSeat);
			CreateReservationDto dto = new CreateReservationDto(1L, 1L, 1000L);

			// when & then
			assertThatThrownBy(() -> reservationFacade.reserve(dto))
					.hasMessage(ConcertErrorCode.ALREADY_RESERVED_SEAT.getReason());
		}

		@Test
		void 실패_동일한_좌석의_예약이_존재함() {
			// given
			ConcertSeat concertSeat = ConcertSeatFixture.create(1L, 2, 1000L, false);
			concertSeatRepository.save(concertSeat);
			Reservation testReservation = ReservationFixture.create(1L, 1, 1000L, ReservationStatus.WAITING, timeProvider.now(), null);
			reservationRepository.save(testReservation);
			CreateReservationDto dto = new CreateReservationDto(1L, 1L, 1000L);

			// when & then
			assertThatThrownBy(() -> reservationFacade.reserve(dto))
					.hasMessage(ReservationErrorCode.ALREADY_SEAT_RESERVATION.getReason());
		}
	}

	@Nested
	class 예약_결제 {
		@Test
		void 성공() {
			// given
			Reservation testReservation = ReservationFixture.create(1L, 1L, 1000L, ReservationStatus.WAITING, timeProvider.now(), null);
			reservationRepository.save(testReservation);

			User testUser = UserFixture.create(1000L);
			userRepository.save(testUser);

			// when
			ReservationPaymentResult sut = reservationFacade.payment(new ReservationPaymentDto(1L, 1L));

			// then
			assertThat(sut.reservationId()).isEqualTo(1L);
			assertThat(sut.remainingPoint()).isZero();

			Optional<Reservation> reservation = reservationRepository.findById(1L);
			assertThat(reservation).isPresent();
			assertThat(reservation.get().getStatus()).isEqualTo(ReservationStatus.CONFIRMED);

			List<UserPointHistory> histoires = userPointHistoryRepository.findByUserId(1L);
			assertThat(histoires).hasSize(1);
			assertThat(histoires.get(0).getAction()).isEqualTo(UserPointAction.USE);
			assertThat(histoires.get(0).getAmount()).isEqualTo(-1000L);

		}

		@Test
		void 실패_잔액부족() {
			// given
			Reservation testReservation = ReservationFixture.create(1L, 1, 9999L, ReservationStatus.WAITING, timeProvider.now(), null);
			reservationRepository.save(testReservation);

			User testUser = UserFixture.create(1000L);
			userRepository.save(testUser);

			// when & then
			assertThatThrownBy(() -> reservationFacade.payment(new ReservationPaymentDto(1L, 1L)))
					.hasMessage(UserErrorCode.NOT_ENOUGH_POINT.getReason());
		}
	}

}
