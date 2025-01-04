package kr.hhplus.be.server.api.concert.presentation.dto;

import kr.hhplus.be.server.api.concert.application.dto.ConcertSeatDto;

import java.util.List;

public record AvailableConcertSeatsResponse(
		List<ConcertSeatDto> seats
) {
}
