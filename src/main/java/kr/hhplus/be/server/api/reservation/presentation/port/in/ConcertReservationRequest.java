package kr.hhplus.be.server.api.reservation.presentation.port.in;

import java.util.Date;

public record ConcertReservationRequest(
		long concertId,
		long seatId,
		Date date
) {
}
