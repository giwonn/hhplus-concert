package kr.hhplus.be.server.api.reservation.domain.producer;


public interface ReservationProducer {
	void send(String topic, String key, Object message);
	void send(String topic, Object message);
}
