package kr.hhplus.be.server.api.token.presentation.dto;

public record TokenRequest(
		long userId,
		long concertId
) {
}
