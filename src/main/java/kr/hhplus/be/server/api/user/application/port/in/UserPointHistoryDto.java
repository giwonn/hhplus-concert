package kr.hhplus.be.server.api.user.application.port.in;

import kr.hhplus.be.server.api.user.application.port.out.UserPointHistoryResult;

import java.time.Instant;

public record UserPointHistoryDto(
		long userId,
		long point,
		Instant transactionAt
) {
	public static UserPointHistoryDto from(UserPointHistoryResult result) {
		return new UserPointHistoryDto(result.userId(), result.point(), result.transactionAt());
	}
}
