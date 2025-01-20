package kr.hhplus.be.server.api.user.domain.entity;

import java.time.Instant;

public class UserPointHistoryFixture {

	public static UserPointHistory createMock(long id, long userId, UserPointAction action, long amount, Instant transactionAt) {
		return new UserPointHistory(id, userId, action, amount, transactionAt);
	}

	public static UserPointHistory create(long userId, UserPointAction action, long amount, Instant transactionAt) {
		return new UserPointHistory(userId, action, amount, transactionAt);
	}
}
