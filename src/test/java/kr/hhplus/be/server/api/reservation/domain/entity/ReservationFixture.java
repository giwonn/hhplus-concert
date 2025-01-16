package kr.hhplus.be.server.api.reservation.domain.entity;

import java.time.Instant;

public class ReservationFixture {

	public static Reservation createMock(long id, long concertSeatId, long userId, long amount, ReservationStatus status, Instant createdAt) {
		return new Reservation(id, concertSeatId, userId, amount, status, createdAt, null);
	}

	public static Reservation createMock(long id, long concertSeatId, long userId, long amount, ReservationStatus status, Instant createdAt, Instant paidAt) {
		return new Reservation(id, concertSeatId, userId, amount, status, createdAt, paidAt);
	}

	public static Reservation create(long concertSeatId, long userId, long amount, ReservationStatus status, Instant createdAt) {
		return new Reservation(concertSeatId, userId, amount, status, createdAt, null);
	}

	public static Reservation create(long concertSeatId, long userId, long amount, ReservationStatus status, Instant createdAt, Instant paidAt) {
		return new Reservation(concertSeatId, userId, amount, status, createdAt, paidAt);
	}
}


