package kr.hhplus.be.server.api.reservation.application.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationOutbox;
import kr.hhplus.be.server.api.reservation.domain.producer.ReservationProducer;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ReservationConfirmedListener {
	private final ReservationOutboxRepository reservationOutboxRepository;
	private final ReservationProducer reservationProducer;
	private final ObjectMapper objectMapper;

	private final String topic = "reservation-confirmed";

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	void saveOutbox(ReservationConfirmedEvent event) throws JsonProcessingException {
		ReservationOutbox outboxEntity = ReservationOutbox.of(
				event.requestId(),
				topic,
				String.valueOf(event.concertSeatId()),
				objectMapper.writeValueAsString(event)
		);
		reservationOutboxRepository.save(outboxEntity);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	void sendMessage(ReservationConfirmedEvent event) {
		reservationProducer.send(
				topic,
				event
		);
	}
}
