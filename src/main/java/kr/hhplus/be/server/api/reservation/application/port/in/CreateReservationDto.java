package kr.hhplus.be.server.api.reservation.application.port.in;

import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;

import java.time.Instant;

public record CreateReservationDto(
		long seatId,
		long userId,
		long amount
) {

	public Reservation to(Instant createdAt) {
		return Reservation.of(seatId, userId, amount, createdAt);
	}
}
