package kr.hhplus.be.server.api.concert.domain.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConcertScheduleFixture {

	public static ConcertSchedule createMock(long id, long concertId, LocalDate concertDate) {
		return new ConcertSchedule(id, concertId, concertDate, new ArrayList<>());
	}

	public static ConcertSchedule createMock(long id, long concertId, LocalDate concertDate, List<ConcertSeat> concertSeats) {
		return new ConcertSchedule(id, concertId, concertDate, concertSeats);
	}

	public static ConcertSchedule create(long concertId, LocalDate concertDate) {
		return new ConcertSchedule(concertId, concertDate);
	}
}


