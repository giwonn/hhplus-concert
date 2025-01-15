package kr.hhplus.be.server.api.token.domain.entity;

import kr.hhplus.be.server.provider.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@RequiredArgsConstructor
@Component
public class TokenFactory {

	private final TimeProvider timeProvider;

	public Token create(long userId, boolean isQueuePassed, Instant expiredAt) {
		return new Token(userId, isQueuePassed, expiredAt);
	}

	public Token createWaiting(long userId) {
		return new Token(userId, false, timeProvider.now().plusSeconds(Token.WAIT_SECONDS));
	}

	public Token createActivate(long userId) {
		return new Token(userId, true, timeProvider.now().plusSeconds(Token.ACTIVATE_SECONDS));
	}
}
