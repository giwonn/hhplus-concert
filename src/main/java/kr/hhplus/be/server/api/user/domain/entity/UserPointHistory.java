package kr.hhplus.be.server.api.user.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
public class UserPointHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long userId;

	@Enumerated(EnumType.STRING)
	private UserPointAction action;

	private long amount;

	private Instant transactionAt;

	UserPointHistory(long id, long userId, UserPointAction action, long amount, Instant transactionAt) {
		this.userId = userId;
		this.action = action;
		this.amount = amount;
		this.transactionAt = transactionAt;
	}

	UserPointHistory(long userId, UserPointAction action, long amount, Instant transactionAt) {
		this.userId = userId;
		this.action = action;
		this.amount = amount;
		this.transactionAt = transactionAt;
	}

	public static UserPointHistory of(long userId, UserPointAction action, long amount, Instant transactionAt) {
		return new UserPointHistory(userId, action, amount, transactionAt);
	}

	public static UserPointHistory createChargeHistory(long userId, long amount, Instant transactionAt) {
		return UserPointHistory.of(userId, UserPointAction.CHARGE, amount, transactionAt);
	}

	public static UserPointHistory createUseHistory(long userId, long amount, Instant transactionAt) {
		return UserPointHistory.of(userId, UserPointAction.USE, amount, transactionAt);
	}

}
