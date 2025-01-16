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
	private Long id;

	private long userId;

	@Enumerated(EnumType.STRING)
	private UserPointAction action;

	private long amount;

	private Instant transactionAt;

	public UserPointHistory(long userId, UserPointAction action, long amount, Instant transactionAt) {
		this.userId = userId;
		this.action = action;
		this.amount = amount;
		this.transactionAt = transactionAt;
	}

}
