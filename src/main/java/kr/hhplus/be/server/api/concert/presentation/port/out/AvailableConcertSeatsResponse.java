package kr.hhplus.be.server.api.concert.presentation.port.out;

import kr.hhplus.be.server.api.concert.application.port.out.ConcertSeatResult;

import java.util.List;

public record AvailableConcertSeatsResponse(
		List<ConcertSeatResult> seats
) {
}
