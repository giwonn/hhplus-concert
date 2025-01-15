package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.api.token.application.port.in.QueueTokenDto;
import kr.hhplus.be.server.api.token.application.port.in.SignQueueTokenDto;
import kr.hhplus.be.server.api.token.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.api.token.domain.entity.Token;
import kr.hhplus.be.server.api.token.domain.repository.TokenRepository;
import kr.hhplus.be.server.api.token.exception.TokenErrorCode;
import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.provider.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final TokenRepository tokenRepository;
	private final TimeProvider timeProvider;
	private final Environment env;

	public QueueTokenResult signQueueToken(SignQueueTokenDto dto) {
		Token token = tokenRepository.save(Token.of(dto.userId(), timeProvider.now()));
		return QueueTokenResult.of(token, token.getId());
	}

	public QueueTokenResult checkQueuePassedAndUpdateToken(QueueTokenDto dto) {
		Token token = tokenRepository.findById(dto.tokenId())
				.orElseThrow(() -> new CustomException(TokenErrorCode.NOT_FOUND_QUEUE));

		Instant now = timeProvider.now();
		if (token.isExpired(now)) throw new CustomException(TokenErrorCode.QUEUE_EXPIRED);

		// 대기열 대기중에 만료시간이 임박한 경우
		if (!token.isQueuePassed() && token.isExpiringSoon(now)) {
			token.setExpiredAt(now.plusSeconds(Token.WAIT_SECONDS));
			tokenRepository.save(token);
		}

		long firstTokenId = tokenRepository.findOldestWaitingTokenId().orElse(0L);
		return QueueTokenResult.of(token, token.getId() - firstTokenId);
	}

	@Scheduled(cron = "*/30 * * * * *")
	@Transactional
	public void activateQueueToken() {
		String limit = env.getProperty("queue.limit.active");
		if (limit == null) {
			throw new IllegalArgumentException("queue.limit.active is required");
		}

		List<Token> tokens = tokenRepository.findOldestTokensByDateAndLimit(timeProvider.now(), Integer.parseInt(limit));

		List<Long> willActivateTokenIds = tokens.stream()
				.filter(token -> !token.isQueuePassed())
				.limit(Integer.parseInt(limit))
				.map(Token::getId)
				.toList();


		tokenRepository.bulkActivateQueue(willActivateTokenIds);
	}

	@Scheduled(cron = "0 */1 * * * *")
	@Transactional
	public void deleteExpiredQueueToken() {
		String limit = env.getProperty("queue.limit.delete");
		if (limit == null) {
			throw new IllegalArgumentException("queue.limit.delete is required");
		}

		tokenRepository.deleteExpiredTokens(timeProvider.now(), Integer.parseInt(limit));
	}

	@Transactional
	public void expireQueueToken(QueueTokenDto dto) {
		tokenRepository.deleteByTokenIdAndUserId(dto.tokenId(), dto.userId());
	}
}
