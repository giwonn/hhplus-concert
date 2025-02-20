package kr.hhplus.be.server.api.reservation.application.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.api.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.api.reservation.domain.producer.ReservationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ReservationConfirmedListener {
	private final ReservationProducer reservationProducer;

	private final String topic = "reservation-confirmed";


	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	void sendMessage(ReservationConfirmedEvent event) {
		reservationProducer.send(
				topic,
				event
		);
	}
}
