package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.reservation.application.port.in.ConfirmReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationResult;
import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationRepository;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationFixture;
import kr.hhplus.be.server.api.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.core.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

	@InjectMocks
	private ReservationService reservationService;

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private TimeProvider timeProvider;

	@BeforeEach
	void setUp() {
		Clock clock = Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), Clock.systemUTC().getZone());
		timeProvider = new TimeProvider(clock);
		reservationRepository = mock(ReservationRepository.class);
		reservationService = new ReservationService(reservationRepository, timeProvider);
	}

	@Nested
	class 예약_만료_처리 {
		@Test
		void 좌석_배정시간이_지난_예약_만료처리_성공() {
			// given
			List<Reservation> reservations = List.of(
					ReservationFixture.createMock(1L, 1L, 1L, 1000L, ReservationStatus.WAITING, timeProvider.now().minusSeconds(Reservation.EXPIRE_SECONDS+1), null),
					ReservationFixture.createMock( 2L, 2L, 2L, 1000L, ReservationStatus.WAITING, timeProvider.now(), null),
					ReservationFixture.createMock(3L, 3L, 3L, 1000L, ReservationStatus.WAITING, timeProvider.now().plusSeconds(10), null)
			);

			when(reservationRepository.findByStatusWithLock(eq(ReservationStatus.WAITING))).thenReturn(reservations);
			when(reservationRepository.updateStatus(any(List.class), eq(ReservationStatus.EXPIRED))).thenReturn(1);
			when(reservationRepository.findAllById(any(List.class))).thenReturn(reservations.subList(0, 1));

			// when
			List<ReservationResult> sut = reservationService.expireReservations();

			// then
			assertAll(() -> {
				verify(reservationRepository).findByStatusWithLock(ReservationStatus.WAITING);
				verify(reservationRepository).updateStatus(reservations.subList(0, 1), ReservationStatus.EXPIRED);
				assertThat(sut).hasSize(1);
				assertThat(sut.get(0).expiredAt()).isBefore(timeProvider.now().plusSeconds(Reservation.EXPIRE_SECONDS));
			});
		}
	}

	@Nested
	class 예약_내역_추가 {
		@Test
		void 성공() {
			// given
			Reservation reservation = ReservationFixture.createMock(
					1L,
					1L,
					1L,
					1000L,
					ReservationStatus.WAITING,
					timeProvider.now()
			);

			when(reservationRepository.findByConcertSeatIdAndStatus(reservation.getId(), ReservationStatus.WAITING)).thenReturn(List.of());
			when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
			CreateReservationDto dto = new CreateReservationDto(1L, 1L, 1000L, Date.valueOf("2024-10-01"));

			// when
			ReservationResult sut = reservationService.reserve(dto);

			// then
			assertAll(() -> {
				assertThat(sut.id()).isEqualTo(reservation.getId());
				assertThat(sut.concertSeatId()).isEqualTo(reservation.getConcertSeatId());
				assertThat(sut.userId()).isEqualTo(reservation.getUserId());
				assertThat(sut.status()).isEqualTo(ReservationStatus.WAITING);
				assertThat(sut.expiredAt()).isEqualTo(timeProvider.now().plusSeconds(Reservation.EXPIRE_SECONDS));
			});
		}

		@Test
		void 실패_이미_예약된_좌석() {
			// given
			Reservation reservation = ReservationFixture.createMock(
					1L,
					1L,
					1L,
					1000L,
					ReservationStatus.WAITING,
					timeProvider.now()
			);

			when(reservationRepository.findByConcertSeatIdAndStatus(reservation.getId(), ReservationStatus.WAITING)).thenReturn(List.of(reservation));
			CreateReservationDto dto = new CreateReservationDto(1L, 1L, 1000L, Date.valueOf("2024-10-01"));

			// when & then
			assertThatThrownBy(() -> reservationService.reserve(dto))
					.hasMessage(ReservationErrorCode.ALREADY_SEAT_RESERVATION.getReason());
		}
	}

	@Nested
	class 예약_내역_조회 {
		@Test
		void 성공() {
			// given
			Reservation reservation = ReservationFixture.createMock(
					1L,
					1L,
					1L,
					1000L,
					ReservationStatus.WAITING,
					timeProvider.now()
			);
			when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

			// when
			ReservationResult sut = reservationService.findById(1L);

			// then
			assertAll(() -> {
				assertThat(sut.id()).isEqualTo(reservation.getId());
				assertThat(sut.status()).isEqualTo(ReservationStatus.WAITING);
			});
		}

		@Test
		void 존재하지_않는_예약() {
			// given
			when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> reservationService.findById(1L))
					.hasMessage(ReservationErrorCode.NOT_FOUND.getReason());

		}
	}

	@Nested
	class 예약_결제_확정 {
		@Test
		void 성공() {
			// given
			Reservation reservation = ReservationFixture.createMock(
					1L,
					1L,
					1L,
					1000L,
					ReservationStatus.WAITING,
					timeProvider.now()
			);
			when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

			Reservation afterReservation = ReservationFixture.createMock(
					1L,
					1L,
					1L,
					1000L,
					ReservationStatus.CONFIRMED,
					timeProvider.now()
			);
			when(reservationRepository.save(any())).thenReturn(afterReservation);

			ConfirmReservationDto dto = new ConfirmReservationDto(1L, timeProvider.now());

			// when
			ReservationResult sut = reservationService.confirmReservation(dto);

			// then
			assertAll(() -> {
				assertThat(sut.id()).isEqualTo(reservation.getId());
				assertThat(sut.status()).isEqualTo(ReservationStatus.CONFIRMED);
			});
		}

		@Test
		void 존재하지_않는_예약() {
			// given
			when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

			ConfirmReservationDto dto = new ConfirmReservationDto(1L, timeProvider.now());

			// when & then
			assertThatThrownBy(() -> reservationService.confirmReservation(dto))
					.hasMessage(ReservationErrorCode.NOT_FOUND.getReason());
		}
	}
}





