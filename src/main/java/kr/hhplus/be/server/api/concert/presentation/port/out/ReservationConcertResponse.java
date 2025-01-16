package kr.hhplus.be.server.api.concert.presentation.port.out;

import kr.hhplus.be.server.api.reservation.application.port.out.ReservationResult;

import java.time.Instant;

public record ReservationConcertResponse(
		long reservationId,
		Instant expireTime
) {
	public static ReservationConcertResponse from(ReservationResult result) {
		return new ReservationConcertResponse(result.id(), result.expiredAt());
	}
}
