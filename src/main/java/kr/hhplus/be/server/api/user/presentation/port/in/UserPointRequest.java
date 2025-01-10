package kr.hhplus.be.server.api.user.presentation.port.in;

public record UserPointRequest(
		long userId,
		long amount
) {
}
