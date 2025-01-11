package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.concert.application.ConcertService;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertSeatResult;
import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.in.ReservationPaymentDto;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationPaymentResult;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationResult;
import kr.hhplus.be.server.api.user.application.UserService;
import kr.hhplus.be.server.api.user.application.port.in.UsePointDto;
import kr.hhplus.be.server.api.user.application.port.out.UserPointResult;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ReservationFacade {

	private final ReservationService reservationService;
	private final ConcertService concertService;
	private final UserService userService;

	@Transactional
	@Scheduled(cron = "0 * * * * *")
	public void expireReservations() {
		List<ReservationResult> expiredReservations = reservationService.expireReservations();
		List<Long> seatIds = expiredReservations.stream()
				.map(ReservationResult::concertSeatId)
				.toList();
		concertService.updateSeatAvailableByIds(seatIds);
	}

	@Transactional
	public ReservationResult reserve(CreateReservationDto dto) {
		ConcertSeatResult concertSeat = concertService.reserveSeat(dto.concertSeatId());
		CreateReservationDto reservationDto = new CreateReservationDto(concertSeat.id(), dto.userId(), concertSeat.amount());
		return reservationService.reserve(reservationDto);
	}

	@Transactional
	public ReservationPaymentResult payment(ReservationPaymentDto dto) {
		ReservationResult reservationResult = reservationService.findByIdWithLock(dto.reservationId());
		UserPointResult userPoint = userService.usePoint(new UsePointDto(dto.userId(), reservationResult.amount()));
		ReservationResult reservation = reservationService.addPaymentTime(dto.reservationId());

		return ReservationPaymentResult.of(reservation.id(), userPoint.point());
	}
}
