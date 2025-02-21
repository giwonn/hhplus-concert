package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.reservation.domain.entity.ReservationOutbox;
import kr.hhplus.be.server.api.reservation.domain.producer.ReservationProducer;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationOutboxRepository;
import kr.hhplus.be.server.api.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.core.enums.OutboxStatus;
import kr.hhplus.be.server.core.exception.CustomException;
import kr.hhplus.be.server.core.provider.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;


@RequiredArgsConstructor
@Service
public class ReservationOutboxService {

	private final ReservationOutboxRepository reservationOutboxRepository;
	private final ReservationProducer reservationProducer;
	private final TimeProvider timeProvider;

	@Transactional
	public void updateOutboxPublished(String requestId) {
		ReservationOutbox outbox = reservationOutboxRepository.findByRequestId(requestId)
				.orElseThrow(() -> new CustomException(ReservationErrorCode.NOT_FOUND));

		outbox.published();
	}

	@Transactional
	public void sendFailureMessages() {
		List<ReservationOutbox> outboxList = reservationOutboxRepository.findByStatus(OutboxStatus.PENDING);

		Instant thresholdTime = timeProvider.now().minusSeconds(300);
		List<ReservationOutbox> failureMessages = outboxList.stream()
				.filter(data -> data.getCreatedAt().isBefore(thresholdTime))
				.toList();

		for (ReservationOutbox message : failureMessages) {
			reservationProducer.send(message.getTopic(), message.getPartitionKey(), message.getMessage());
		}
	}

}
