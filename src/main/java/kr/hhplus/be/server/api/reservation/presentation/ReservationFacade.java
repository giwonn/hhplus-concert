package kr.hhplus.be.server.api.reservation.presentation;

import kr.hhplus.be.server.annotation.logexcutiontime.LogExecutionTime;
import kr.hhplus.be.server.api.concert.application.ConcertService;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertSeatResult;
import kr.hhplus.be.server.api.reservation.application.ReservationService;
import kr.hhplus.be.server.api.reservation.application.port.in.ConfirmReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.in.ReservationPaymentDto;
import kr.hhplus.be.server.api.reservation.application.port.in.ReserveSeatDto;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationPaymentResult;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationResult;
import kr.hhplus.be.server.api.user.application.UserService;
import kr.hhplus.be.server.api.user.application.port.in.UserPointDto;
import kr.hhplus.be.server.api.user.application.port.in.UserPointHistoryDto;
import kr.hhplus.be.server.api.user.application.port.out.UserPointHistoryResult;
import kr.hhplus.be.server.provider.CompensationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ReservationFacade {

	private final ReservationService reservationService;
	private final ConcertService concertService;
	private final UserService userService;
	private final CompensationProvider compensationProvider;

	@Scheduled(cron = "0 * * * * *")
	@LogExecutionTime
	public void expireReservations() {
		List<ReservationResult> expiredReservations = reservationService.expireReservations();
		List<Long> seatIds = expiredReservations.stream().map(ReservationResult::concertSeatId).toList();
		concertService.unReserveSeats(seatIds);
	}

	public ReservationResult reserve(ReserveSeatDto dto) {
		return compensationProvider.handle(compensations -> {
			ConcertSeatResult concertSeat = concertService.reserveSeat(dto.seatId());
			compensations.add(() -> concertService.unReserveSeat(concertSeat.id()));

			CreateReservationDto reservationDto = new CreateReservationDto(
					concertSeat.id(), dto.userId(), concertSeat.amount(), dto.date());
			return reservationService.reserve(reservationDto);
		});
	}

	public ReservationPaymentResult payment(ReservationPaymentDto dto) {
		ReservationResult reservation = reservationService.findById(dto.reservationId());

		return compensationProvider.handle(compensations -> {
			UserPointHistoryResult usedPoint = userService.usePoint(new UserPointDto(reservation.userId(), reservation.amount()));
			compensations.add(() -> userService.rollbackPoint(UserPointHistoryDto.from(usedPoint)));

			ConfirmReservationDto confirmReservationDto = new ConfirmReservationDto(dto.reservationId(), usedPoint.transactionAt());
			ReservationResult reservationResult = reservationService.confirmReservation(confirmReservationDto);
			return ReservationPaymentResult.of(reservationResult.id(), usedPoint.point());
		});
	}
}
