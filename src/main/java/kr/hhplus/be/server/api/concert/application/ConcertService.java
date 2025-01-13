package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.api.concert.application.port.out.ConcertScheduleResult;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertSeatResult;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeat;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertScheduleRepository;
import kr.hhplus.be.server.api.concert.domain.repository.ConcertSeatRepository;
import kr.hhplus.be.server.api.concert.exception.ConcertErrorCode;
import kr.hhplus.be.server.common.exception.CustomException;
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
	public void updateSeatAvailableByIds(List<Long> concertSeatIds) {
		concertSeatRepository.updateSeatAvailableByIds(concertSeatIds);
	}

	@Transactional(readOnly = true)
	public List<ConcertScheduleResult> getReservableSchedules(Long concertId) {
		List<ConcertSchedule> concertSchedules = concertScheduleRepository.findByConcertIdAndIsSoldOutFalse(concertId);
		return concertSchedules.stream().map(ConcertScheduleResult::from).toList();
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
		ConcertSeat seat = concertSeatRepository.findByIdWithLock(seatId)
				.orElseThrow(() -> new CustomException(ConcertErrorCode.NOT_FOUND_SEAT));

		if (seat.isReserved()) throw new CustomException(ConcertErrorCode.ALREADY_RESERVED_SEAT);

		seat.reserve();

		return ConcertSeatResult.from(concertSeatRepository.save(seat));
	}
}
