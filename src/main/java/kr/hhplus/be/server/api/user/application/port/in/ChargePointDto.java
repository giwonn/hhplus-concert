package kr.hhplus.be.server.api.user.application.port.in;

import kr.hhplus.be.server.api.user.domain.entity.User;

public record ChargePointDto(
		long userId,
		long point
) {
	public static ChargePointDto from(User user) {
		return new ChargePointDto(user.getId(), user.getPoint());
	}
}
