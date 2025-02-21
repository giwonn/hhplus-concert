package kr.hhplus.be.server.api.reservation.presentation;

import kr.hhplus.be.server.api.reservation.application.ReservationOutboxService;
import kr.hhplus.be.server.api.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.api.reservation.application.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class ReservationConsumer {

	private final ReservationOutboxService reservationOutboxService;

	@KafkaListener(id = "reservation-created-self-consume", topics = "reservation-created")
	public void reservationCreated(ConsumerRecord<String, ReservationCreatedEvent> record) {
		ReservationCreatedEvent event = record.value();
		reservationOutboxService.updateOutboxPublished(event.requestId());
	}

	@KafkaListener(id = "reservation-confirmed-self-consume", topics = "reservation-confirmed")
	public void reservationConfirmed(ConsumerRecord<String, ReservationConfirmedEvent> record) {
		ReservationConfirmedEvent event = record.value();
		reservationOutboxService.updateOutboxPublished(event.requestId());
	}
}
