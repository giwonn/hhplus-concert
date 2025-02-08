package kr.hhplus.be.server.api.queue.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class WaitingToken {

	private long userId;

	private long waitingNumber;

	public WaitingToken(long userId, long waitingNumber) {
		this.userId = userId;
		this.waitingNumber = waitingNumber;
	}

	public static WaitingToken of(long userId, long waitingNumber) {
		return new WaitingToken(userId, waitingNumber);
	}

}
