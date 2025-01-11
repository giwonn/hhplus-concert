package kr.hhplus.be.server.api.reservation.application.port.out;

import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;

import java.time.Instant;

public record ReservationResult(
		long id,
		long concertSeatId,
		long userId,
		long amount,
		ReservationStatus status,
		Instant createdAt,
		Instant paidAt
) {
	public static ReservationResult from(Reservation entity) {
		return new ReservationResult(
				entity.getId(),
				entity.getConcertSeatId(),
				entity.getUserId(),
				entity.getAmount(),
				entity.getStatus(),
				entity.getCreatedAt(),
				entity.getPaidAt()
		);
	}
}
