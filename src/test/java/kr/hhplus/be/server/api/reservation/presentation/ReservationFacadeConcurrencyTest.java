package kr.hhplus.be.server.api.reservation.presentation;

import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeat;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeatFixture;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertSeatRepository;
import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.in.ReserveSeatDto;
import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationRepository;
import kr.hhplus.be.server.base.BaseIntegrationTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import kr.hhplus.be.server.util.ConcurrencyTestUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@Import(FixedClockBean.class)
class ReservationFacadeConcurrencyTest extends BaseIntegrationTest {

	@Autowired
	ReservationFacade reservationFacade;

	@Autowired
	ReservationRepository reservationRepository;

	@Autowired
	ConcertSeatRepository concertSeatRepository;

	@Nested
	class 좌석_동시_예약 {

		@Test
		void 스무명중_단_한명만_성공() throws InterruptedException {
			// given
			ConcertSeat concertSeat = ConcertSeatFixture.create(3L, 1, 1000L, false);
			concertSeatRepository.save(concertSeat);

			List<Supplier<?>> tasks = new ArrayList<>();
			int tryCount = 20;
			for (long i = 1; i <= tryCount; i++) {
				final long userId = i;
				tasks.add(() -> reservationFacade.reserve(new ReserveSeatDto(1L, userId, 1000L, LocalDateTime.parse("2024-10-01T12:00:00"))));
			}

			ConcurrencyTestUtil.Result result = ConcurrencyTestUtil.run(tasks);

			// then
			List<Reservation> list = reservationRepository.findAll();
			assertThat(list).hasSize(1);
			assertThat(result.successCount()).isEqualTo(1);
			assertThat(result.failCount()).isEqualTo(tryCount - 1);
		}

	}

}
