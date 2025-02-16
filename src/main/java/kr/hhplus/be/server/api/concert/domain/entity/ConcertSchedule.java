package kr.hhplus.be.server.api.concert.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(name = "uk_concert_id_date", columnNames = { "concert_id", "concert_date" })
})
@Getter
@NoArgsConstructor
public class ConcertSchedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private long concertId;

	@Column(nullable = false, columnDefinition = "DATETIME")
	private LocalDateTime concertDate;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "concertScheduleId")
	private List<ConcertSeat> concertSeats = new ArrayList<>();

	ConcertSchedule(long id, long concertId, LocalDateTime concertDate, List<ConcertSeat> concertSeats) {
		this.id = id;
		this.concertId = concertId;
		this.concertDate = concertDate;
		this.concertSeats = concertSeats;
	}

	ConcertSchedule(long concertId, LocalDateTime concertDate) {
		this.concertId = concertId;
		this.concertDate = concertDate;
	}

	public boolean isAvailable() {
		return concertSeats.stream().anyMatch(seat -> !seat.isReserved());
	}

}
