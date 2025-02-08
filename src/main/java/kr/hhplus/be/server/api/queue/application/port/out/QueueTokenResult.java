package kr.hhplus.be.server.api.queue.application.port.out;

import kr.hhplus.be.server.api.queue.domain.entity.ActiveToken;
import kr.hhplus.be.server.api.queue.domain.entity.WaitingToken;

import java.time.Instant;

public record QueueTokenResult(
		long userId,
		long waitingNumber,
		Instant expiredAt
) {

	public static QueueTokenResult from(WaitingToken token) {
		return new QueueTokenResult(token.getUserId(), token.getWaitingNumber(), null);
	}

	public static QueueTokenResult from(ActiveToken token) {
		return new QueueTokenResult(token.getUserId(), 0, token.getExpiredAt());
	}
}
