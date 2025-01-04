package kr.hhplus.be.server.api.concert.application.dto;

public record ConcertSeatDto(
		long id,
		long seatNum,
		long price
) {
}
