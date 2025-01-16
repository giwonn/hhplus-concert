package kr.hhplus.be.server.api.reservation.application.port.in;

import java.util.Date;

public record CreateReservationDto(
		long concertSeatId,
		long userId,
		long amount,
		Date date
) {
}
