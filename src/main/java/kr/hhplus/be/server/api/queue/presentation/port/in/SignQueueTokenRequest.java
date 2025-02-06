package kr.hhplus.be.server.api.queue.presentation.port.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.api.queue.application.port.in.SignQueueTokenDto;

public record SignQueueTokenRequest(
		@NotNull
		@Schema(description = "사용자 ID", example = "1")
		Long userId
) {
	public SignQueueTokenDto toDto() {
		return new SignQueueTokenDto(userId);
	}
}
