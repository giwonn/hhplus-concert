package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.api.concert.application.port.out.ConcertScheduleResult;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertSeatResult;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeat;
import kr.hhplus.be.server.api.concert.domain.entity.TestConcertScheduleFactory;
import kr.hhplus.be.server.api.concert.domain.entity.TestConcertSeatFactory;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertScheduleRepository;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertSeatRepository;
import kr.hhplus.be.server.api.concert.exception.ConcertErrorCode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConcertServiceTest {

	@InjectMocks
	private ConcertService concertService;

	@Mock
	private ConcertSeatRepository concertSeatRepository;

	@Mock
	private ConcertScheduleRepository concertScheduleRepository;

	@Nested
	class 예약_가능_스케쥴_조회 {
		@Test
		void 성공() {
			// given
			List<ConcertSchedule> schedules = List.of(
					TestConcertScheduleFactory.createMock(1L, 1L, LocalDate.parse("2025-01-01"), false),
					TestConcertScheduleFactory.createMock(3L, 1L, LocalDate.parse("2025-01-03"), false)
			);

			when(concertScheduleRepository.findByConcertIdAndIsSoldOutFalse(1L)).thenReturn(schedules);

			// when
			List<ConcertScheduleResult> sut = concertService.getReservableSchedules(1L);

			// then
			assertAll(() -> {
				assertThat(sut.get(0).id()).isEqualTo(1L);
				assertThat(sut.get(0).concertDate()).isEqualTo(LocalDate.parse("2025-01-01"));
				assertThat(sut.get(0).isSoldOut()).isFalse();

				assertThat(sut.get(1).id()).isEqualTo(3L);
				assertThat(sut.get(1).concertDate()).isEqualTo(LocalDate.parse("2025-01-03"));
				assertThat(sut.get(0).isSoldOut()).isFalse();
			});
		}
	}


	@Nested
	class 예약_가능_좌석_조회 {
		@Test
		void 예약_가능한_콘서트_좌석을_조회한다() {
			// given
			List<ConcertSeat> seats = List.of(
					TestConcertSeatFactory.createMock(1L, 1L, 1, 1000L, false),
					TestConcertSeatFactory.createMock(3L, 1L, 3, 1000L, false)
			);

			when(concertSeatRepository.findByConcertScheduleIdAndIsReservedFalse(1L)).thenReturn(seats);

			// when
			List<ConcertSeatResult> sut = concertService.getReservableSeats(1L);

			// then
			assertAll(() -> {
				assertThat(sut.get(0).id()).isEqualTo(1L);
				assertThat(sut.get(0).concertScheduleId()).isEqualTo(1L);
				assertThat(sut.get(0).seatNum()).isEqualTo(1);

				assertThat(sut.get(1).id()).isEqualTo(3L);
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
			ConcertSeat seat = TestConcertSeatFactory.createMock(1L, 1L, 1, 1000L, false);
			when(concertSeatRepository.findByIdWithLock(seat.getId())).thenReturn(Optional.of(seat));
			when(concertSeatRepository.save(any(ConcertSeat.class))).thenReturn(seat);

			// when
			ConcertSeatResult sut = concertService.reserveSeat(seat.getId());

			// then
			verify(concertSeatRepository, times(1)).save(seat);
			assertThat(sut.id()).isEqualTo(seat.getId());
			assertThat(sut.concertScheduleId()).isEqualTo(seat.getConcertScheduleId());
			assertThat(sut.seatNum()).isEqualTo(seat.getSeatNum());
			assertThat(sut.isReserved()).isTrue();
		}

		@Test
		void 실패_이미예약됨() {
			// given
			ConcertSeat seat = TestConcertSeatFactory.createMock(1L, 1L, 1, 1000L, true);
			when(concertSeatRepository.findByIdWithLock(seat.getId())).thenReturn(Optional.of(seat));

			// when & then
			assertThatThrownBy(() -> concertService.reserveSeat(1L)).hasMessage(ConcertErrorCode.ALREADY_RESERVED_SEAT.getReason());
		}

		@Test
		void 실패_존재하지_않는_좌석() {
			// given
			when(concertSeatRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> concertService.reserveSeat(1L)).hasMessage(ConcertErrorCode.NOT_FOUND_SEAT.getReason());
		}

	}




}
