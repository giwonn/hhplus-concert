package kr.hhplus.be.server.api.concert.domain.entity;

import java.time.LocalDate;

public class TestConcertScheduleFactory {

	public static ConcertSchedule createMock(long id, long concertId, LocalDate concertDate, boolean isSoldOut) {
		return new ConcertSchedule(id, concertId, concertDate, isSoldOut);
	}

	public static ConcertSchedule create(long concertId, LocalDate concertDate, boolean isSoldOut) {
		return new ConcertSchedule(concertId, concertDate, isSoldOut);
	}
}


