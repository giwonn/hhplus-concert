package kr.hhplus.be.server.api.concert.domain.entity;

public class TestConcertSeatFactory {

	public static ConcertSeat createMock(long id, long concertScheduleId, int seatNum, long amount, boolean isReserved) {
		return new ConcertSeat(id, concertScheduleId, seatNum, amount, isReserved);
	}

	public static ConcertSeat create(long concertScheduleId, int seatNum, long amount, boolean isReserved) {
		return new ConcertSeat(concertScheduleId, seatNum, amount, isReserved);
	}
}


