package kr.hhplus.be.server.api.queue.domain.entity;

import java.time.Instant;

public class TokenFixture {

	public static ActiveToken createActive(long userId) {
		return new ActiveToken(userId, Instant.now());
	}

	public static ActiveToken createActive(long userId, Instant expiredAt) {
		return new ActiveToken(userId, expiredAt);
	}

	public static WaitingToken createWaiting(long userId, long waitingNumber) {
		return new WaitingToken(userId, waitingNumber);
	}
}


