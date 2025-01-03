package kr.hhplus.be.server.api.token.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.Instant;

@Entity
@Getter
public class Token {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long concertId;

	private long userId;

	private boolean isActivated;

	private Instant expiredAt;

}
