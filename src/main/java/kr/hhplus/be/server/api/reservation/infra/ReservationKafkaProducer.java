package kr.hhplus.be.server.api.reservation.infra;

import kr.hhplus.be.server.api.reservation.domain.producer.ReservationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class ReservationKafkaProducer implements ReservationProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void send(String topic, String key, Object message) {
		kafkaTemplate.send(topic, key, message);
	}

	public void send(String topic, Object message) {
		kafkaTemplate.send(topic, message);
	}
}
