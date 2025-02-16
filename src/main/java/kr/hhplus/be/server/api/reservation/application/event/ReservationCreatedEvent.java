package kr.hhplus.be.server.api.reservation.application.event;

import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;

import java.time.Instant;

public record ReservationCreatedEvent(
		long id,
		long concertSeatId,
		long userId,
		long amount,
		ReservationStatus status,
		Instant createdAt,
		Instant paidAt
) {

	public static ReservationCreatedEvent from(Reservation reservation) {
		return new ReservationCreatedEvent(
				reservation.getId(),
				reservation.getConcertSeatId(),
				reservation.getUserId(),
				reservation.getAmount(),
				reservation.getStatus(),
				reservation.getCreatedAt(),
				reservation.getPaidAt()
		);
	}
}
