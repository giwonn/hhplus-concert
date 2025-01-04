package kr.hhplus.be.server.api.reservation.presentation.dto;

public record ReservationPaymentRequest(
		long userId,
		long reservationId
) {
}
