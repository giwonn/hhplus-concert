package kr.hhplus.be.server.api.concert.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConcertSeatTest {

	@Test
	void 좌석_예약_성공() {
		// given
		ConcertSeat seat = TestConcertSeatFactory.createMock(1L, 1L, 1, 1000L, false);

		// when
		seat.reserve();

		// then
		assertThat(seat.isReserved()).isTrue();
	}

	@Test
	void 좌석_예약_해제_성공() {
		// given
		ConcertSeat seat = TestConcertSeatFactory.createMock(1L, 1L, 1, 1000L, true);

		// when
		seat.unReserve();

		// then
		assertThat(seat.isReserved()).isFalse();
	}
}
