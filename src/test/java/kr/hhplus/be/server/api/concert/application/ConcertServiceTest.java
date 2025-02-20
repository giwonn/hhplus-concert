package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.api.concert.application.port.out.ConcertScheduleResult;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertSeatResult;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeat;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertScheduleFixture;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeatFixture;
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
import java.time.LocalDateTime;
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
					ConcertScheduleFixture.createMock(1L, 1L, LocalDateTime.parse("2025-01-01T12:00"),
							List.of(ConcertSeatFixture.createMock(1L, 1L, 1, 1000L, false))),
					ConcertScheduleFixture.createMock(3L, 1L, LocalDateTime.parse("2025-01-03T12:00"),
							List.of(ConcertSeatFixture.createMock(1L, 1L, 1, 1000L, true)))
			);
			when(concertScheduleRepository.findByConcertId(1L)).thenReturn(schedules);

			// when
			List<ConcertScheduleResult> sut = concertService.getReservableSchedules(1L);

			// then
			assertAll(() -> {
				assertThat(sut).hasSize(1);
				assertThat(sut.get(0).id()).isEqualTo(1L);
				assertThat(sut.get(0).concertDate()).isEqualTo("2025-01-01T12:00:00");
			});
		}
	}


	@Nested
	class 예약_가능_좌석_조회 {
		@Test
		void 예약_가능한_콘서트_좌석을_조회한다() {
			// given
			List<ConcertSeat> seats = List.of(
					ConcertSeatFixture.createMock(1L, 1L, 1, 1000L, false),
					ConcertSeatFixture.createMock(3L, 1L, 3, 1000L, false)
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
			ConcertSeat seat = ConcertSeatFixture.createMock(1L, 1L, 1, 1000L, false);
			when(concertSeatRepository.findById(seat.getId())).thenReturn(Optional.of(seat));

			// when
			ConcertSeatResult sut = concertService.reserveSeat(seat.getId());

			// then
			assertThat(sut.id()).isEqualTo(seat.getId());
			assertThat(sut.concertScheduleId()).isEqualTo(seat.getConcertScheduleId());
			assertThat(sut.seatNum()).isEqualTo(seat.getSeatNum());
			assertThat(sut.isReserved()).isTrue();
		}

		@Test
		void 실패_이미예약됨() {
			// given
			ConcertSeat seat = ConcertSeatFixture.createMock(1L, 1L, 1, 1000L, true);
			when(concertSeatRepository.findById(seat.getId())).thenReturn(Optional.of(seat));

			// when & then
			assertThatThrownBy(() -> concertService.reserveSeat(1L)).hasMessage(ConcertErrorCode.ALREADY_RESERVED_SEAT.getReason());
		}

		@Test
		void 실패_존재하지_않는_좌석() {
			// given
			when(concertSeatRepository.findById(1L)).thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> concertService.reserveSeat(1L)).hasMessage(ConcertErrorCode.NOT_FOUND_SEAT.getReason());
		}

	}

	@Nested
	class 콘서트_좌석_예약_해제 {
		@Test
		void 성공() {
			// given
			ConcertSeat seat = ConcertSeatFixture.createMock(1L, 1L, 1, 1000L, true);
			when(concertSeatRepository.findByIdWithLock(seat.getId())).thenReturn(Optional.of(seat));

			// when
			ConcertSeatResult sut = concertService.unReserveSeat(seat.getId());

			// then
			assertThat(sut.id()).isEqualTo(seat.getId());
			assertThat(sut.concertScheduleId()).isEqualTo(seat.getConcertScheduleId());
			assertThat(sut.seatNum()).isEqualTo(seat.getSeatNum());
			assertThat(sut.isReserved()).isFalse();
		}

		@Test
		void 실패_존재하지_않는_좌석() {
			// given
			when(concertSeatRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> concertService.unReserveSeat(1L))
					.hasMessage(ConcertErrorCode.NOT_FOUND_SEAT.getReason());
		}

	}




}
