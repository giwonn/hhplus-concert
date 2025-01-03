package kr.hhplus.be.server.api.concert.presentation.dto;

import java.util.Date;

public record ConcertReservationRequest(
		long concertId,
		long seatId,
		Date date
) {
}
