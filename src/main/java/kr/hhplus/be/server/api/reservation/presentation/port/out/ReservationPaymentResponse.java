package kr.hhplus.be.server.api.reservation.presentation.port.out;

public record ReservationPaymentResponse(
		long reservationId,
		long remainingPoint
) {
}
