package kr.hhplus.be.server.api.user.presentation.port.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UserPointRequest(
		@NotNull
		@Schema(description = "사용자 ID", example = "1")
		Long userId,

		@NotNull
		@Min(1)
		@Schema(description = "포인트", example = "1000")
		Long amount
) {
}
