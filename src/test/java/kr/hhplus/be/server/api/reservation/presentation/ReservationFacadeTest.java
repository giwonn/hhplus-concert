package kr.hhplus.be.server.api.reservation.presentation;


import kr.hhplus.be.server.api.concert.application.ConcertService;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertSeatResult;
import kr.hhplus.be.server.api.reservation.application.ReservationService;
import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.in.ReservationPaymentDto;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationPaymentResult;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationResult;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;
import kr.hhplus.be.server.api.user.application.UserService;
import kr.hhplus.be.server.api.user.application.port.in.UserPointDto;
import kr.hhplus.be.server.api.user.application.port.out.UserPointHistoryResult;
import kr.hhplus.be.server.common.provider.FixedTimeProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationFacadeTest {

	@InjectMocks
	ReservationFacade reservationFacade;

	@Mock
	ReservationService reservationService;

	@Mock
	ConcertService concertService;

	@Mock
	UserService userService;

	@Nested
	class 예약_만료 {

		@Test
		void 예약이_만료되면_좌석이_해제된다() {
			// given
			List<ReservationResult> reservationResults = List.of(
					new ReservationResult(1L, 1L, 1L, 1000L, ReservationStatus.EXPIRED, Instant.now(), null),
					new ReservationResult(1L, 2L, 1L, 1000L, ReservationStatus.EXPIRED, Instant.now(), null),
					new ReservationResult(1L, 3L, 1L, 1000L, ReservationStatus.EXPIRED, Instant.now(), null)
			);
			ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
			when(reservationService.expireReservations()).thenReturn(reservationResults);

			// when
			reservationFacade.expireReservations();

			// then
			verify(concertService).unReserveSeats(captor.capture());
			assertThat(captor.getValue()).containsExactlyInAnyOrder(1L, 2L, 3L);
		}
	}

	@Nested
	class 좌석_예약 {
		@Test
		void 성공() {
			// given
			ConcertSeatResult concertSeatResult = new ConcertSeatResult(1L, 1L, 1, 1000L, true);
			when(concertService.reserveSeat(anyLong())).thenReturn(concertSeatResult);

			CreateReservationDto dto = new CreateReservationDto(1L, 1L, 1000L, Date.valueOf("2024-10-01"));
			ReservationResult reservationResult = new ReservationResult(1L, 1L, 1L, 1000L, ReservationStatus.WAITING, Instant.now(), null);
			when(reservationService.reserve(any(CreateReservationDto.class))).thenReturn(reservationResult);

			// when
			ReservationResult result = reservationFacade.reserve(dto);

			// then
			assertThat(result.id()).isEqualTo(1L);
			assertThat(result.concertSeatId()).isEqualTo(1L);
			assertThat(result.status()).isEqualTo(ReservationStatus.WAITING);
		}
	}

	@Nested
	class 예약_결제 {
		@Test
		void 성공() {
			// given

			ReservationResult reservation = new ReservationResult(1L, 1L, 1L, 1000L, ReservationStatus.WAITING, Instant.now(), null);
			when(reservationService.findById(anyLong())).thenReturn(reservation);
			when(userService.usePoint(any(UserPointDto.class))).thenReturn(new UserPointHistoryResult(1L, 0L, FixedTimeProvider.FIXED_TIME));

			ReservationResult paidReservation = new ReservationResult(1L, 1L, 1L, 1000L, ReservationStatus.CONFIRMED, Instant.now(), Instant.now());
			when(reservationService.confirmReservation(any())).thenReturn(paidReservation);

			ReservationPaymentDto dto = new ReservationPaymentDto(1L, 1L);

			// when
			ReservationPaymentResult sut = reservationFacade.payment(dto);

			// then
			assertThat(sut.reservationId()).isEqualTo(1L);
			assertThat(sut.remainingPoint()).isZero();
		}
	}

}
