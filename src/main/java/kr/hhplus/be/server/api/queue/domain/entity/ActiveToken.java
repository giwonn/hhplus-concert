package kr.hhplus.be.server.api.queue.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@Getter
public class ActiveToken {

	public static final int ACTIVATE_SECONDS = 60 * 10;

	private long userId;

	private Instant expiredAt;

	ActiveToken(long userId, Instant expiredTime) {
		this.userId = userId;
		this.expiredAt = expiredTime;
	}

	public static ActiveToken of(long userId, double expiredTime) {
		return new ActiveToken(
				userId,
				Instant.ofEpochSecond((long) expiredTime)
		);
	}

	public static ActiveToken of(long userId, Instant expiredTime) {
		return new ActiveToken(
				userId,
				expiredTime
		);
	}

}
