package kr.hhplus.be.server.api.concert.presentation.port.out;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertScheduleResult;

import java.time.LocalDateTime;
import java.util.List;

public record AvailableConcertSchedulesResponse(
		@Schema(description = "콘서트 일정 목록")
		List<Schedule> schedules
) {

	public static AvailableConcertSchedulesResponse from(List<ConcertScheduleResult> result) {
		return new AvailableConcertSchedulesResponse(
				result.stream()
						.map(schedule -> new Schedule(schedule.concertId(), schedule.id(), schedule.concertDate()))
						.toList()
		);
	}

	public record Schedule(
			long concertId,
			long concertScheduleId,
			LocalDateTime concertDate
	) {}
}
