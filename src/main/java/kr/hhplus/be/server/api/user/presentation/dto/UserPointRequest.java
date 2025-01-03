package kr.hhplus.be.server.api.user.presentation.dto;

public record UserPointRequest(
		long userId,
		long amount
) {
}
