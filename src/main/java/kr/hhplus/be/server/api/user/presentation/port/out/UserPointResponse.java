package kr.hhplus.be.server.api.user.presentation.port.out;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.api.user.application.port.out.UserPointResult;

public record UserPointResponse(
		@Schema(description = "사용자 ID", example = "1")
		long userId,
		@Schema(description = "포인트", example = "1000")
		long point
) {
	public static UserPointResponse from(UserPointResult result) {
		return new UserPointResponse(
				result.userId(),
				result.point()
		);
	}
}
