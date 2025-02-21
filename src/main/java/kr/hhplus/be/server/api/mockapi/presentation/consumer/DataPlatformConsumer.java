package kr.hhplus.be.server.api.mockapi.presentation.consumer;

import kr.hhplus.be.server.api.mockapi.application.DataPlatformSendService;
import kr.hhplus.be.server.api.mockapi.application.port.in.ReservationConfirmedDto;
import kr.hhplus.be.server.api.reservation.application.event.ReservationConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class DataPlatformConsumer {

	private final DataPlatformSendService dataPlatformSendService;

	@KafkaListener(id = "reservation-confirmed", topics = "reservation-confirmed")
	public void reservationConfirmed(ConsumerRecord<String, ReservationConfirmedEvent> record) {
		ReservationConfirmedEvent event = record.value();
		ReservationConfirmedDto dto = new ReservationConfirmedDto(
				event.reservationId(),
				event.concertSeatId(),
				event.userId(),
				event.amount(),
				event.status(),
				event.createdAt(),
				event.paidAt()
		);
		dataPlatformSendService.sendReservation(dto);
	}
}
