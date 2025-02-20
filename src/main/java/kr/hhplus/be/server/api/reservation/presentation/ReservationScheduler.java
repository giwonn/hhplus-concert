package kr.hhplus.be.server.api.reservation.presentation;

import kr.hhplus.be.server.api.reservation.application.ReservationOutboxService;
import kr.hhplus.be.server.api.reservation.application.ReservationService;
import kr.hhplus.be.server.core.annotation.logexcutiontime.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class ReservationScheduler {

	private final ReservationFacade reservationFacade;
	private final ReservationOutboxService reservationOutboxService;

	@Scheduled(cron = "0 * * * * *")
	@LogExecutionTime
	public void expireReservations() {
		reservationFacade.expireReservations();
	}

	@Scheduled(cron = "0 * * * * *")
	@LogExecutionTime
	public void sendFailureMessages() {
		reservationOutboxService.sendFailureMessages();
	}
}
