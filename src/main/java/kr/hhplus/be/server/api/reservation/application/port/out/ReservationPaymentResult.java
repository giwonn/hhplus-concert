package kr.hhplus.be.server.api.reservation.application.port.out;

public record ReservationPaymentResult(
		long reservationId,
		long remainingPoint
) {
	public static ReservationPaymentResult of(long reservationId, long remainingPoint) {
		return new ReservationPaymentResult(reservationId, remainingPoint);
	}
}
