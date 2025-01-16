package kr.hhplus.be.server.api.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {

	public static final long EXPIRE_SECONDS = 5 * 60;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private long concertSeatId;

	private long userId;

	private long amount;

	@Setter
	@Enumerated(EnumType.STRING)
	private ReservationStatus status;

	private Instant createdAt;

	private Instant paidAt;

	Reservation(long id, long concertSeatId, long userId, long amount, ReservationStatus status, Instant createdAt, Instant paidAt) {
		this.id = id;
		this.concertSeatId = concertSeatId;
		this.userId = userId;
		this.amount = amount;
		this.status = status;
		this.createdAt = createdAt;
		this.paidAt = paidAt;
	}

	Reservation(long concertSeatId, long userId, long amount, ReservationStatus status, Instant createdAt, Instant paidAt) {
		this.concertSeatId = concertSeatId;
		this.userId = userId;
		this.amount = amount;
		this.status = status;
		this.createdAt = createdAt;
		this.paidAt = paidAt;
	}

	public static Reservation of(long concertSeatId, long userId, long amount, Instant createdAt) {
		return new Reservation(concertSeatId, userId, amount, ReservationStatus.WAITING, createdAt, null);
	}

	public void addPaymentTime(Instant paymentTime) {
		this.paidAt = paymentTime;
	}

}
