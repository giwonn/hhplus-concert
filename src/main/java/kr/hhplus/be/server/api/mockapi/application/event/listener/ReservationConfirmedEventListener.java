package kr.hhplus.be.server.api.mockapi.application.event.listener;

import kr.hhplus.be.server.api.mockapi.application.DataPlatformSendService;
import kr.hhplus.be.server.api.mockapi.application.port.in.ReservationConfirmedDto;
import kr.hhplus.be.server.api.reservation.application.event.ReservationConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ReservationConfirmedEventListener {
	private final DataPlatformSendService sendService;

	public void sendReservation(ReservationConfirmedEvent event) {
		ReservationConfirmedDto dto = new ReservationConfirmedDto(
				event.reservationId(),
				event.concertSeatId(),
				event.userId(),
				event.amount(),
				event.status(),
				event.createdAt(),
				event.paidAt()
		);

		sendService.sendReservation(dto);
	}

}
