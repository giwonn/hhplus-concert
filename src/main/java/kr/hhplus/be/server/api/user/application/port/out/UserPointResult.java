package kr.hhplus.be.server.api.user.application.port.out;

import kr.hhplus.be.server.api.user.domain.entity.User;

public record UserPointResult(
		long userId,
		long point
) {
	public static UserPointResult from(User user) {
		return new UserPointResult(user.getId(), user.getPoint());
	}

}
