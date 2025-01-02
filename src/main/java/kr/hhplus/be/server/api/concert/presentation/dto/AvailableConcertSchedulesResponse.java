package kr.hhplus.be.server.api.concert.presentation.dto;

import java.util.List;

public record AvailableConcertSchedulesResponse(
		long concertId,
		List<String> dates
) {
}
