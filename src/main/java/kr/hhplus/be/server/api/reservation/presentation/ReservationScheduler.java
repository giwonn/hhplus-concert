package kr.hhplus.be.server.api.reservation.presentation;

import kr.hhplus.be.server.core.annotation.logexcutiontime.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class ReservationScheduler {

	private final ReservationFacade reservationFacade;

	@Scheduled(cron = "0 * * * * *")
	@LogExecutionTime
	public void expireReservations() {
		reservationFacade.expireReservations();
	}
}
