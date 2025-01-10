package kr.hhplus.be.server.api.reservation.application.port.in;

public record CreateReservationDto(
		long concertSeatId,
		long userId,
		long amount
) {
}
