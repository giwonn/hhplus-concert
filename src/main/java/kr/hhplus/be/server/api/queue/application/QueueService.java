package kr.hhplus.be.server.api.queue.application;

import kr.hhplus.be.server.api.queue.domain.entity.ActiveToken;
import kr.hhplus.be.server.api.queue.domain.entity.WaitingToken;
import kr.hhplus.be.server.api.queue.application.port.in.QueueTokenDto;
import kr.hhplus.be.server.api.queue.application.port.in.SignQueueTokenDto;
import kr.hhplus.be.server.api.queue.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.api.queue.domain.repository.QueueRepository;
import kr.hhplus.be.server.api.queue.exception.TokenErrorCode;
import kr.hhplus.be.server.core.exception.CommonErrorCode;
import kr.hhplus.be.server.core.exception.CustomException;
import kr.hhplus.be.server.core.provider.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

	private final QueueRepository queueRepository;
	private final TimeProvider timeProvider;
	private final Environment env;

	public QueueTokenResult signQueueToken(SignQueueTokenDto dto) {
		queueRepository.pushToWaitingQueue(dto.userId());
		WaitingToken waitingToken = queueRepository.getWaitingToken(dto.userId())
				.orElseThrow(() -> new CustomException(TokenErrorCode.NOT_FOUND_QUEUE));

		return QueueTokenResult.from(waitingToken);
	}

	public Optional<QueueTokenResult> getActiveQueueToken(QueueTokenDto dto) {
		Optional<ActiveToken> activeToken = queueRepository.getActiveToken(dto.userId());
		if (activeToken.isEmpty()) return Optional.empty();
		if (activeToken.get().getExpiredAt().isBefore(timeProvider.now())) return Optional.empty();

		return Optional.of(QueueTokenResult.from(activeToken.get()));
	}

	public QueueTokenResult getWaitingQueueToken(QueueTokenDto dto) {
		Optional<WaitingToken> waitingToken = queueRepository.getWaitingToken(dto.userId());
		if (waitingToken.isEmpty()) throw new CustomException(TokenErrorCode.NOT_FOUND_QUEUE);

		return QueueTokenResult.from(waitingToken.get());
	}

	public void activateWaitingTokens() {
		String limit = env.getProperty("queue.limit.active");
		if (limit == null) {
			log.error("application.yml - queue.limit.active is required");
			throw new CustomException(CommonErrorCode.MISSING_ENV);
		}

		List<Long> willActiveUserIds = queueRepository.getWaitingUserIds(Integer.parseInt(limit));
		if (willActiveUserIds.isEmpty()) return;

		queueRepository.pushToActiveQueue(willActiveUserIds, timeProvider.now().plusSeconds(ActiveToken.ACTIVATE_SECONDS));
		queueRepository.removeWaitingTokens(willActiveUserIds);
	}

	public void removeExpiredQueueTokens() {
		queueRepository.removeExpiredTokens();
	}

	public void removeActiveTokenByUserId(Long userId) {
		queueRepository.removeActiveTokenByUserId(userId);
	}
}
