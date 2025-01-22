package kr.hhplus.be.server.api.concert.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ConcertSeat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private long concertScheduleId;

	private int seatNum;

	private long amount;

	private boolean isReserved;

	@Version
	private long version;

	ConcertSeat(long id, long concertScheduleId, int seatNum, long amount, boolean isReserved) {
		this.id = id;
		this.concertScheduleId = concertScheduleId;
		this.seatNum = seatNum;
		this.amount = amount;
		this.isReserved = isReserved;
	}

	ConcertSeat(long concertScheduleId, int seatNum, long amount, boolean isReserved) {
		this.concertScheduleId = concertScheduleId;
		this.seatNum = seatNum;
		this.amount = amount;
		this.isReserved = isReserved;
	}

	public void reserve() {
		this.isReserved = true;
	}

	public void unReserve() {
		this.isReserved = false;
	}
}
