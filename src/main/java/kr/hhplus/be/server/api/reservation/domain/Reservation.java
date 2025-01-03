package kr.hhplus.be.server.api.reservation.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Getter
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long concertSeatId;

	private long userId;

	private long amount;

	@Enumerated(EnumType.STRING)
	private ReservationStatus status;

	private Instant expiredAt;
}
