package kr.hhplus.be.server.api.mockapi.application.port.in;

import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;

import java.time.Instant;

public record ReservationConfirmedDto(
		long id,
		long concertSeatId,
		long userId,
		long amount,
		ReservationStatus status,
		Instant createdAt,
		Instant paidAt
) {
}
