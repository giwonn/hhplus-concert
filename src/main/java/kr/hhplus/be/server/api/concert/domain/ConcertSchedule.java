package kr.hhplus.be.server.api.concert.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Table(indexes = @Index(name = "idx_concert_id_date", columnList = "concert_id, concert_date"))
@Getter
public class ConcertSchedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long concertId;

	private LocalDate concertDate;

	private boolean isSoldOut;

}
