package kr.hhplus.be.server.api.token.presentation.dto;

import java.time.Instant;

public record TokenResponse(
		long id,
		Instant expiredAt
) {
}
