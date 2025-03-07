package kr.hhplus.be.server.api.reservation.presentation.port.in;

import jakarta.validation.constraints.NotNull;

public record ReservationPaymentRequest(
		@NotNull
		long userId,

		@NotNull
		long reservationId
) {
}
