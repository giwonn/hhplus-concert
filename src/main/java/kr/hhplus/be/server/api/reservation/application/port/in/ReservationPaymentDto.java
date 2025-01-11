package kr.hhplus.be.server.api.reservation.application.port.in;

public record ReservationPaymentDto(
		long reservationId,
		long userId
) {
}
