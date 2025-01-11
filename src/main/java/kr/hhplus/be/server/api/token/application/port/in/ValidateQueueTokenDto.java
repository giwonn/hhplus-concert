package kr.hhplus.be.server.api.token.application.port.in;

public record ValidateQueueTokenDto(
		long tokenId,
		long userId
) {
}
