package kr.hhplus.be.server.api.reservation.presentation.dto;

public record ReservationPaymentResponse(
		long reservationId,
		long remainingPoint
) {
}
