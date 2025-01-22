package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.api.concert.application.port.out.ConcertScheduleResult;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertSeatResult;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeat;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertScheduleRepository;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertSeatRepository;
import kr.hhplus.be.server.api.concert.exception.ConcertErrorCode;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ConcertService {

	private final ConcertSeatRepository concertSeatRepository;
	private final ConcertScheduleRepository concertScheduleRepository;

	@Transactional
	public void unReserveSeats(List<Long> concertSeatIds) {
		concertSeatRepository.updateSeatReservableByIds(concertSeatIds);
	}

	@Transactional(readOnly = true)
	public List<ConcertScheduleResult> getReservableSchedules(Long concertId) {
		List<ConcertSchedule> concertSchedules = concertScheduleRepository.findByConcertId(concertId);
		return concertSchedules.stream().filter(ConcertSchedule::isAvailable).map(ConcertScheduleResult::from).toList();
	}

	@Transactional(readOnly = true)
	public List<ConcertSeatResult> getReservableSeats(long concertScheduleId) {
		return concertSeatRepository.findByConcertScheduleIdAndIsReservedFalse(concertScheduleId)
				.stream()
				.map(ConcertSeatResult::from)
				.toList();
	}

	@Transactional
	public ConcertSeatResult reserveSeat(long seatId) {
		ConcertSeat seat = concertSeatRepository.findById(seatId)
				.orElseThrow(() -> new CustomException(ConcertErrorCode.NOT_FOUND_SEAT));

		// 좌석 예약이 되어있으면 예외를 던짐
		if (seat.isReserved()) throw new CustomException(ConcertErrorCode.ALREADY_RESERVED_SEAT);

		// 좌석 예약 발급
		seat.reserve();

		return ConcertSeatResult.from(seat);
	}

	@Transactional
	public ConcertSeatResult unReserveSeat(long seatId) {
		ConcertSeat seat = concertSeatRepository.findByIdWithLock(seatId)
				.orElseThrow(() -> new CustomException(ConcertErrorCode.NOT_FOUND_SEAT));

		seat.unReserve();

		return ConcertSeatResult.from(seat);
	}
}
