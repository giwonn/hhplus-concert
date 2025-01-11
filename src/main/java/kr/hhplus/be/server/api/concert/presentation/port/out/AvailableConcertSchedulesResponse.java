package kr.hhplus.be.server.api.concert.presentation.port.out;

import java.util.List;

public record AvailableConcertSchedulesResponse(
		long concertId,
		List<String> dates
) {
}
