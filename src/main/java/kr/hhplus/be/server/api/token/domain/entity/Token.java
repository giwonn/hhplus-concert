package kr.hhplus.be.server.api.token.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;

@NoArgsConstructor
@Getter
@Entity
public class Token {

	public static final int WAIT_SECONDS = 60 * 3;
	public static final int WAIT_THRESHOLD_SECONDS = 10;
	public static final int ACTIVATE_SECONDS = 60 * 10;

	@Id
	private Long id;

	private long userId;

	private boolean isQueuePassed;

	@Setter
	@Column(name = "expired_at")
	private Instant expiredAt;

	Token(long id, long userId, boolean isQueuePassed, Instant expiredAt) {
		this.id = id;
		this.userId = userId;
		this.isQueuePassed = isQueuePassed;
		this.expiredAt = expiredAt;
	}

	Token(long userId, boolean isQueuePassed, Instant expiredAt) {
		this.userId = userId;
		this.isQueuePassed = isQueuePassed;
		this.expiredAt = expiredAt;
	}

	public static Token of(long userId, Instant expiredAt) {
		return new Token(userId, false, expiredAt);
	}

	public boolean isExpiringSoon(Instant time) {
		return Duration.between(time, expiredAt).getSeconds() < WAIT_THRESHOLD_SECONDS;
	}

	public boolean isExpired(Instant time) {
		return expiredAt.isBefore(time);
	}

}
