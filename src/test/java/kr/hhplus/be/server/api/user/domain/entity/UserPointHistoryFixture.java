package kr.hhplus.be.server.api.user.domain.entity;

import java.time.Instant;

public class UserPointHistoryFixture {

	public static UserPointHistory create(long userId, UserPointAction action, long amount, Instant transactionAt) {
		return new UserPointHistory(userId, action, amount, transactionAt);
	}
}
