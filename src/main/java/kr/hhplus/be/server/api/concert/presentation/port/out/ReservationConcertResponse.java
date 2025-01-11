package kr.hhplus.be.server.api.concert.presentation.port.out;

import java.time.Instant;

public record ReservationConcertResponse(
		long reservationId,
		Instant expireTime
) {
}
