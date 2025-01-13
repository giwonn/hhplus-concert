package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationResult;
import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationRepository;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;
import kr.hhplus.be.server.api.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.provider.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;

	private final TimeProvider timeProvider;

	public List<ReservationResult> expireReservations() {
		List<Reservation> expireReservations = reservationRepository.findByStatus(ReservationStatus.WAITING)
				.stream()
				.filter(reservation -> reservation.getCreatedAt().isBefore(timeProvider.now()))
				.toList();

		reservationRepository.updateStatus(expireReservations, ReservationStatus.EXPIRED);

		List<Reservation> expiredReservations = reservationRepository.findAllById(
				expireReservations.stream().map(Reservation::getId).toList());

		return expiredReservations.stream().map(ReservationResult::from).toList();
 	}

	public ReservationResult reserve(CreateReservationDto dto) {
		if (!reservationRepository.findByConcertSeatIdAndStatus(dto.concertSeatId(), ReservationStatus.WAITING).isEmpty()) {
			throw new CustomException(ReservationErrorCode.DUPLICATE_SEAT_RESERVATION);
		}
		Reservation reservation = Reservation.of(dto.concertSeatId(), dto.userId(), dto.amount(), timeProvider.now());
		return ReservationResult.from(reservationRepository.save(reservation));
	}

	public ReservationResult findByIdWithLock(long reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new CustomException(ReservationErrorCode.NOT_FOUND));

		return ReservationResult.from(reservation);
	}

	public ReservationResult addPaymentTime(long reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new CustomException(ReservationErrorCode.NOT_FOUND));

		reservation.addPaymentTime(timeProvider.now());
		reservation.setStatus(ReservationStatus.CONFIRMED);

		return ReservationResult.from(reservationRepository.save(reservation));
	}
}
