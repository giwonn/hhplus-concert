package kr.hhplus.be.server.api.concert.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ConcertSeat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long concertScheduleId;

	private long amount;

	private boolean isReserved;

}
