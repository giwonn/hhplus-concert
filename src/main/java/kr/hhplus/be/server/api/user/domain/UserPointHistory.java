package kr.hhplus.be.server.api.user.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Getter
public class UserPointHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long userId;

	@Enumerated(EnumType.STRING)
	private UserPointAction action;

	private long amount;

	private Instant transactionAt;

}
