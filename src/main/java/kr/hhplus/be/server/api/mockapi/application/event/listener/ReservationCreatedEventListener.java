package kr.hhplus.be.server.api.mockapi.application.event.listener;

import kr.hhplus.be.server.api.mockapi.application.DataPlatformSendService;
import kr.hhplus.be.server.api.mockapi.application.port.in.ReservationCreatedDto;
import kr.hhplus.be.server.api.reservation.application.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ReservationCreatedEventListener {
	private final DataPlatformSendService sendService;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void reservationCreatedHandler(ReservationCreatedEvent event) {
		sendService.sendReservation(ReservationCreatedDto.from(event));
	}

}
