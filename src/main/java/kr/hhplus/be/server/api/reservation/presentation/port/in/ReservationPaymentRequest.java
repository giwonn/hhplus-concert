package kr.hhplus.be.server.api.reservation.presentation.port.in;

public record ReservationPaymentRequest(
		long userId,
		long reservationId
) {
}
