package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.reservation.domain.entity.ReservationOutbox;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationOutboxRepository;
import kr.hhplus.be.server.api.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.core.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class ReservationOutboxService {

	private final ReservationOutboxRepository reservationOutboxRepository;

	@Transactional
	public void updateOutboxPublished(String requestId) {
		ReservationOutbox outbox = reservationOutboxRepository.findByRequestId(requestId)
				.orElseThrow(() -> new CustomException(ReservationErrorCode.NOT_FOUND));

		outbox.published();
	}


}
