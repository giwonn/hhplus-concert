package kr.hhplus.be.server.api.concert.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConcertScheduleTest {

	@Test
	void 콘서트_좌석_예약_가능() {
		// given
		List<ConcertSeat> seats = List.of(
				ConcertSeatFixture.createMock(1L, 1L, 1, 1000L, false),
				ConcertSeatFixture.createMock(1L, 1L, 2, 1000L, false),
				ConcertSeatFixture.createMock(2L, 1L, 3, 1000L, true)
		);

		ConcertSchedule schedule = ConcertScheduleFixture.createMock(1L, 1L, LocalDate.parse("2025-01-01"), seats);

		// when & then
		assertThat(schedule.isAvailable()).isTrue();
	}

	@Test
	void 콘서트_좌석_매진() {
		// given
		List<ConcertSeat> seats = List.of(
				ConcertSeatFixture.createMock(1L, 1L, 1, 1000L, true),
				ConcertSeatFixture.createMock(1L, 1L, 2, 1000L, true),
				ConcertSeatFixture.createMock(2L, 1L, 3, 1000L, true)
		);

		ConcertSchedule schedule = ConcertScheduleFixture.createMock(1L, 1L, LocalDate.parse("2025-01-01"), seats);

		// when & then
		assertThat(schedule.isAvailable()).isFalse();
	}
}
