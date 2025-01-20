package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.reservation.application.port.in.ConfirmReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationResult;
import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationRepository;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;
import kr.hhplus.be.server.api.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.provider.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;

	private final TimeProvider timeProvider;

	@Transactional
	public List<ReservationResult> expireReservations() {
		List<Reservation> expireReservations = reservationRepository.findByStatusWithLock(ReservationStatus.WAITING)
				.stream()
				.filter(reservation -> reservation.getCreatedAt().isBefore(timeProvider.now()))
				.toList();

		reservationRepository.updateStatus(expireReservations, ReservationStatus.EXPIRED);

		List<Reservation> expiredReservations = reservationRepository.findAllById(
				expireReservations.stream().map(Reservation::getId).toList());

		return expiredReservations.stream().map(ReservationResult::from).toList();
 	}

	@Transactional
	public ReservationResult reserve(CreateReservationDto dto) {
		List<Reservation> duplicateReservations = reservationRepository.findByConcertSeatIdAndStatus(dto.concertSeatId(), ReservationStatus.WAITING);
		if (!duplicateReservations.isEmpty()) {
			throw new CustomException(ReservationErrorCode.ALREADY_SEAT_RESERVATION);
		}
		Reservation reservation = Reservation.of(dto.concertSeatId(), dto.userId(), dto.amount(), timeProvider.now());
		return ReservationResult.from(reservationRepository.save(reservation));
	}

	@Transactional(readOnly = true)
	public ReservationResult findById(long reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new CustomException(ReservationErrorCode.NOT_FOUND));

		return ReservationResult.from(reservation);
	}

	@Transactional
	public ReservationResult confirmReservation(ConfirmReservationDto dto) {
		Reservation reservation = reservationRepository.findById(dto.reservationId())
				.orElseThrow(() -> new CustomException(ReservationErrorCode.NOT_FOUND));

		reservation.confirm(dto.transactionAt());

		return ReservationResult.from(reservationRepository.save(reservation));
	}
}
