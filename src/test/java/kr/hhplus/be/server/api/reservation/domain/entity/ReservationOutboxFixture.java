package kr.hhplus.be.server.api.reservation.domain.entity;

import java.time.Instant;
import java.util.UUID;

public class ReservationOutboxFixture {

	public static ReservationOutbox create(Instant createdAt) {
		return ReservationOutbox.of(
				UUID.randomUUID().toString(),
				"test-topic",
				"test-partition-key",
				"{ \"testKey\": \"testValue\" }",
				createdAt
		);
	}
}
