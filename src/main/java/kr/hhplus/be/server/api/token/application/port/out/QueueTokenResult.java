package kr.hhplus.be.server.api.token.application.port.out;

import kr.hhplus.be.server.api.token.domain.entity.Token;

import java.time.Instant;

public record QueueTokenResult(
		long id,
		long userId,
		long waitingNumber,
		Instant expiredAt
) {

	public static QueueTokenResult of(Token token, long waitingNumber) {
		return new QueueTokenResult(token.getId(), token.getUserId(), waitingNumber, token.getExpiredAt());
	}
}
