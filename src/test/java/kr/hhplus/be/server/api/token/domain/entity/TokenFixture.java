package kr.hhplus.be.server.api.token.domain.entity;

import java.time.Instant;

public class TokenFixture {

	public static Token createMock(long id, long userId, boolean isQueuePassed, Instant expiredAt) {
		return new Token(id, userId, isQueuePassed, expiredAt);
	}

	public static Token create(long userId, boolean isQueuePassed, Instant expiredAt) {
		return new Token(userId, isQueuePassed, expiredAt);
	}
}


