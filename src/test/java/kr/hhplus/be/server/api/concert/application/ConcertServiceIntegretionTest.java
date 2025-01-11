package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.api.concert.application.port.out.ConcertScheduleResult;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertSeatResult;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeat;
import kr.hhplus.be.server.api.concert.domain.entity.TestConcertScheduleFactory;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertScheduleRepository;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertSeatRepository;
import kr.hhplus.be.server.api.concert.domain.entity.TestConcertSeatFactory;
import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.base.BaseIntegretionTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ConcertServiceIntegretionTest extends BaseIntegretionTest {

	@Autowired
	ConcertService concertService;

	@Autowired
	ConcertScheduleRepository concertScheduleRepository;

	@Autowired
	ConcertSeatRepository concertSeatRepository;

	@Nested
	class 좌석_배정_해제 {
		@Test
		void concertSeatId를_받아서_임시_배정을_해제한다() {
			// when
			final List<ConcertSeat> concertSeats = List.of(
					TestConcertSeatFactory.create( 1L, 1, 1000L, false),
					TestConcertSeatFactory.create( 2L, 1, 1000L, true),
					TestConcertSeatFactory.create( 3L, 1, 1000L, false)
			);
			concertSeatRepository.saveAll(concertSeats);

			List<Long> concertSeatIds = List.of(1L, 2L);

			// then
			concertService.updateSeatAvailableByIds(concertSeatIds);

			Optional<ConcertSeat> expiredReservation = concertSeatRepository.findById(2L);
			System.out.println(expiredReservation.get().isReserved());
			assertAll(() -> {
				assertThat(expiredReservation).isPresent();
				assertThat(expiredReservation.get().isReserved()).isFalse();
			});
		}
	}

	@Nested
	class 예약_가능한_콘서트_스케쥴_조회 {
		@Test
		void 성공() {
			// given
			final List<ConcertSchedule> concertSchedules = List.of(
					TestConcertScheduleFactory.create(1L, LocalDate.parse("2025-01-01"), false),
					TestConcertScheduleFactory.create(1L, LocalDate.parse("2025-01-02"), true),
					TestConcertScheduleFactory.create(1L, LocalDate.parse("2025-01-03"), false)
			);
			concertScheduleRepository.saveAll(concertSchedules);

			// when
			List<ConcertScheduleResult> sut = concertService.getReservableSchedules(1L)
					.stream().sorted(Comparator.comparingLong(ConcertScheduleResult::id)).toList();

			// then
			assertAll(() -> {
				assertThat(sut.get(0).id()).isEqualTo(1L);
				assertThat(sut.get(0).concertDate()).isEqualTo("2025-01-01");
				assertThat(sut.get(0).isSoldOut()).isFalse();

				assertThat(sut.get(1).id()).isEqualTo(3L);
				assertThat(sut.get(1).concertDate()).isEqualTo("2025-01-03");
				assertThat(sut.get(1).isSoldOut()).isFalse();
			});
		}
	}

	@Nested
	class 예약_가능한_콘서트_좌석_조회 {
		@Test
		void 성공() {
			// given
			final List<ConcertSeat> concertSeats = List.of(
					TestConcertSeatFactory.create(1L, 1, 1000L, false),
					TestConcertSeatFactory.create(1L, 2, 1000L, true),
					TestConcertSeatFactory.create(1L, 3, 1000L, false)
			);
			concertSeatRepository.saveAll(concertSeats);

			// when
			List<ConcertSeatResult> sut = concertService.getReservableSeats(1L)
					.stream().sorted(Comparator.comparingLong(ConcertSeatResult::seatNum)).toList();

			// then
			assertAll(() -> {
				assertThat(sut.get(0).concertScheduleId()).isEqualTo(1L);
				assertThat(sut.get(0).seatNum()).isEqualTo(1);

				assertThat(sut.get(1).concertScheduleId()).isEqualTo(1L);
				assertThat(sut.get(1).seatNum()).isEqualTo(3);
			});
		}
	}

	@Nested
	class 콘서트_좌석_예약 {
		@Test
		void 성공() {
			// given
			final ConcertSeat concertSeat = TestConcertSeatFactory.create(1L, 1, 1000L, false);
			concertSeatRepository.save(concertSeat);

			// when
			ConcertSeatResult sut = concertService.reserveSeat(1L);

			// then
			assertAll(() -> {
				assertThat(sut.id()).isEqualTo(concertSeat.getId());
				assertThat(sut.concertScheduleId()).isEqualTo(concertSeat.getConcertScheduleId());
				assertThat(sut.seatNum()).isEqualTo(concertSeat.getSeatNum());
				assertThat(sut.isReserved()).isTrue();
			});
		}
	}



}
