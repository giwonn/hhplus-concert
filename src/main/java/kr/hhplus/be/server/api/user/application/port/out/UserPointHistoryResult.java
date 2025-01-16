package kr.hhplus.be.server.api.user.application.port.out;

import java.time.Instant;

public record UserPointHistoryResult(
		long userId,
		long point,
		Instant transactionAt
) {
	public static UserPointHistoryResult of(long userId, long point, Instant transactionAt) {
		return new UserPointHistoryResult(userId, point, transactionAt);
	}

}
