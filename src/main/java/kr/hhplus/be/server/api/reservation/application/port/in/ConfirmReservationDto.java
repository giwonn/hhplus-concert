package kr.hhplus.be.server.api.reservation.application.port.in;

import java.time.Instant;

public record ConfirmReservationDto(
		long reservationId,
		Instant transactionAt
) {
}
