package kr.hhplus.be.server.api.concert.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(indexes = @Index(name = "idx_concert_id", columnList = "concert_id"))
@Getter
@NoArgsConstructor
public class ConcertSchedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private long concertId;

	private LocalDate concertDate;

	private boolean isSoldOut;

	ConcertSchedule(long id, long concertId, LocalDate concertDate, boolean isSoldOut) {
		this.id = id;
		this.concertId = concertId;
		this.concertDate = concertDate;
		this.isSoldOut = isSoldOut;
	}

	ConcertSchedule(long concertId, LocalDate concertDate, boolean isSoldOut) {
		this.concertId = concertId;
		this.concertDate = concertDate;
		this.isSoldOut = isSoldOut;
	}

}
