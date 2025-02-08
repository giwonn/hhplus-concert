package kr.hhplus.be.server.api.queue.presentation.port.out;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.api.queue.application.port.out.QueueTokenResult;

import java.time.Instant;

public record QueueTokenResponse(
		@Schema(description = "유저 ID", example = "1")
		long userId,

		@Schema(description = "대기번호", example = "30")
		long waitingNumber,

		@Schema(description = "만료시간", example = "2024-01-01T12:00:00Z")
		Instant expiredAt
) {

	public static QueueTokenResponse from(QueueTokenResult token) {
		return new QueueTokenResponse(
				token.userId(),
				token.waitingNumber(),
				token.expiredAt()
		);
	}
}
