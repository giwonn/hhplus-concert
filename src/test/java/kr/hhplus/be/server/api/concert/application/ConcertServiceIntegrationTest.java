package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.api.concert.application.port.out.ConcertScheduleResult;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertSeatResult;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeat;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertScheduleFixture;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertScheduleRepository;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertSeatRepository;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeatFixture;
import kr.hhplus.be.server.base.BaseIntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ConcertServiceIntegrationTest extends BaseIntegrationTest {

	@Autowired
	ConcertService concertService;

	@Autowired
	ConcertScheduleRepository concertScheduleRepository;

	@Autowired
	ConcertSeatRepository concertSeatRepository;

	@Nested
	class 좌석_배정_일괄_해제 {
		@Test
		void concertSeatId_리스트를_받아서_임시_배정을_해제한다() {
			// when
			final List<ConcertSeat> concertSeats = List.of(
					ConcertSeatFixture.create( 1L, 1, 1000L, false),
					ConcertSeatFixture.create( 2L, 1, 1000L, true),
					ConcertSeatFixture.create( 3L, 1, 1000L, false)
			);
			concertSeatRepository.saveAll(concertSeats);

			List<Long> concertSeatIds = List.of(1L, 2L);

			// then
			concertService.unReserveSeats(concertSeatIds);

			Optional<ConcertSeat> expiredReservation = concertSeatRepository.findById(2L);
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
					ConcertScheduleFixture.create(1L, LocalDateTime.parse("2025-01-01T12:00"))
			);
			concertScheduleRepository.saveAll(concertSchedules);

			final List<ConcertSeat> seats = List.of(
					ConcertSeatFixture.create(1L, 1, 1000L, false),
					ConcertSeatFixture.create(1L, 2, 1000L, true),
					ConcertSeatFixture.create(1L, 3, 1000L, true)
			);
			concertSeatRepository.saveAll(seats);

			// when
			List<ConcertScheduleResult> sut = concertService.getReservableSchedules(1L)
					.stream().sorted(Comparator.comparingLong(ConcertScheduleResult::id)).toList();

			// then
			assertAll(() -> {
				assertThat(sut.get(0).id()).isEqualTo(1L);
				assertThat(sut.get(0).concertDate()).isEqualTo("2025-01-01T12:00:00");
			});
		}
	}

	@Nested
	class 예약_가능한_콘서트_좌석_조회 {
		@Test
		void 성공() {
			// given
			final List<ConcertSeat> concertSeats = List.of(
					ConcertSeatFixture.create(1L, 1, 1000L, false),
					ConcertSeatFixture.create(1L, 2, 1000L, true),
					ConcertSeatFixture.create(1L, 3, 1000L, false)
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
			final ConcertSeat concertSeat = ConcertSeatFixture.create(1L, 1, 1000L, false);
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

	@Nested
	class 콘서트_좌석_단건_예약_해제 {
		@Test
		void 성공() {
			// given
			ConcertSeat concertSeat = ConcertSeatFixture.create(1L, 1, 1000L, true);
			concertSeatRepository.save(concertSeat);

			// when
			ConcertSeatResult sut = concertService.unReserveSeat(concertSeat.getId());

			// then
			assertAll(() -> {
				assertThat(sut.id()).isEqualTo(concertSeat.getId());
				assertThat(sut.concertScheduleId()).isEqualTo(concertSeat.getConcertScheduleId());
				assertThat(sut.seatNum()).isEqualTo(concertSeat.getSeatNum());
				assertThat(sut.isReserved()).isFalse();
			});
		}
	}



}
