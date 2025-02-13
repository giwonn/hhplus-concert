package kr.hhplus.be.server.api.mockapi.application.port.in;

import kr.hhplus.be.server.api.reservation.application.event.ReservationCreatedEvent;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;

import java.time.Instant;

public record ReservationCreatedDto(
		long id,
		long concertSeatId,
		long userId,
		long amount,
		ReservationStatus status,
		Instant createdAt,
		Instant paidAt
) {
	public static ReservationCreatedDto from(ReservationCreatedEvent reservation) {
		return new ReservationCreatedDto(
				reservation.id(),
				reservation.concertSeatId(),
				reservation.userId(),
				reservation.amount(),
				reservation.status(),
				reservation.createdAt(),
				reservation.paidAt()
		);
	}
}
