package kr.hhplus.be.server.api.concert.presentation.dto;

import java.time.Instant;

public record ConcertReservationResponse(
		long reservationId,
		Instant expireTime
) {
}
