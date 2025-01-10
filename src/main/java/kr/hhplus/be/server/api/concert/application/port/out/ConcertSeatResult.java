package kr.hhplus.be.server.api.concert.application.port.out;

import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeat;

public record ConcertSeatResult(
		long id,
		long concertScheduleId,
		long seatNum,
		long amount,
		boolean isReserved
) {
	public static ConcertSeatResult from(ConcertSeat concertSeat) {
		return new ConcertSeatResult(concertSeat.getId(), concertSeat.getConcertScheduleId(), concertSeat.getSeatNum(), concertSeat.getAmount(), concertSeat.isReserved());
	}
}
