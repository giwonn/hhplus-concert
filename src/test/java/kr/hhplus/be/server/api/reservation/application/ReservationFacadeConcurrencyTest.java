package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeat;
import kr.hhplus.be.server.api.concert.domain.entity.TestConcertSeatFactory;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertSeatRepository;
import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;
import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationRepository;
import kr.hhplus.be.server.base.BaseIntegretionTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Import(FixedClockBean.class)
class ReservationFacadeConcurrencyTest extends BaseIntegretionTest {

	@Autowired
	ReservationFacade reservationFacade;

	@Autowired
	ReservationRepository reservationRepository;

	@Autowired
	ConcertSeatRepository concertSeatRepository;

	@Nested
	class 좌석_동시_예약 {

		@Test
		void 다섯명중_단_한명만_성공() throws InterruptedException {
			// given
			ConcertSeat concertSeat = TestConcertSeatFactory.create(3L, 1, 1000L, false);
			concertSeatRepository.save(concertSeat);
			CreateReservationDto dto = new CreateReservationDto(1L, 4L, 1000L);

			int tryCount = 10;
			List<Callable<Void>> tasks = new ArrayList<>();
			for (long i = 1; i <= tryCount; i++) {
				tasks.add(() -> {
					reservationFacade.reserve(dto);
					return null;
				});
			}
			ExecutorService executorService = Executors.newFixedThreadPool(tryCount);

			AtomicInteger successCount = new AtomicInteger();
			AtomicInteger failCount = new AtomicInteger();

			// when
			List<Future<Void>> futures = executorService.invokeAll(tasks);
			for (Future<Void> future : futures) {
				try {
					future.get();
					successCount.incrementAndGet();
				} catch (Exception e) {
					failCount.incrementAndGet();
				}
			}
			executorService.shutdown();

			// then
			List<Reservation> list = reservationRepository.findAll();
			assertThat(list).hasSize(1);
			assertThat(successCount.get()).isEqualTo(1);
			assertThat(failCount.get()).isEqualTo(9);
		}

	}

}
