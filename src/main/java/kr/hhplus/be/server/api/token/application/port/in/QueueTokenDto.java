package kr.hhplus.be.server.api.token.application.port.in;

public record QueueTokenDto(
		long tokenId,
		long userId
) {
}
