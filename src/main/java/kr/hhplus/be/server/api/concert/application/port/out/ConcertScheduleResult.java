package kr.hhplus.be.server.api.concert.application.port.out;

import kr.hhplus.be.server.api.concert.domain.entity.ConcertSchedule;

import java.time.LocalDate;

public record ConcertScheduleResult(
		long id,
		long concertId,
		LocalDate concertDate,
		boolean isSoldOut
) {
	public static ConcertScheduleResult from(ConcertSchedule concertSchedule) {
		return new ConcertScheduleResult(
				concertSchedule.getId(),
				concertSchedule.getConcertId(),
				concertSchedule.getConcertDate(),
				concertSchedule.isSoldOut()
		);
	}
}
