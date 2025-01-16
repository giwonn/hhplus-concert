package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.annotation.logexcutiontime.LogExecutionTime;
import kr.hhplus.be.server.api.token.application.port.in.QueueTokenDto;
import kr.hhplus.be.server.api.token.application.port.in.SignQueueTokenDto;
import kr.hhplus.be.server.api.token.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.api.token.domain.entity.Token;
import kr.hhplus.be.server.api.token.domain.entity.TokenFactory;
import kr.hhplus.be.server.api.token.domain.repository.TokenRepository;
import kr.hhplus.be.server.api.token.exception.TokenErrorCode;
import kr.hhplus.be.server.exception.CommonErrorCode;
import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.provider.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

	private final TokenRepository tokenRepository;
	private final TokenFactory tokenFactory;
	private final TimeProvider timeProvider;
	private final Environment env;

	public QueueTokenResult signQueueToken(SignQueueTokenDto dto) {
		Token token = tokenRepository.save(tokenFactory.createWaiting(dto.userId()));
		long firstWaitingTokenId = tokenRepository.findOldestWaitingTokenId().orElse(token.getId());
		return QueueTokenResult.of(token, token.getWaitingNumber(firstWaitingTokenId));
	}

	@Transactional
	public QueueTokenResult checkQueuePassedAndUpdateToken(QueueTokenDto dto) {
		Token token = tokenRepository.findByIdAndUserIdWithLock(dto.tokenId(), dto.userId())
				.orElseThrow(() -> new CustomException(TokenErrorCode.NOT_FOUND_QUEUE));

		Instant now = timeProvider.now();
		if (token.isExpired(now)) throw new CustomException(TokenErrorCode.QUEUE_EXPIRED);

		// 대기열 대기중에 만료시간이 임박한 경우
		if (!token.isQueuePassed() && token.isExpiringSoon(now)) {
			token.setExpiredAt(now.plusSeconds(Token.WAIT_SECONDS));
			tokenRepository.save(token);
		}

		long firstWaitingTokenId = tokenRepository.findOldestWaitingTokenId().orElse(token.getId());
		return QueueTokenResult.of(token, token.getWaitingNumber(firstWaitingTokenId));
	}

	@Scheduled(cron = "*/30 * * * * *")
	@LogExecutionTime
	@Transactional
	public void activateQueueToken() {
		String limit = env.getProperty("queue.limit.active");
		if (limit == null) {
			log.error("application.yml - queue.limit.active is required");
			throw new CustomException(CommonErrorCode.MISSING_ENV);
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
	@LogExecutionTime
	@Transactional
	public void deleteExpiredQueueTokens() {
		String limit = env.getProperty("queue.limit.delete");
		if (limit == null) {
			log.error("application.yml - queue.limit.delete is required");
			throw new CustomException(CommonErrorCode.MISSING_ENV);
		}

		tokenRepository.deleteExpiredTokens(timeProvider.now(), Integer.parseInt(limit));
	}

	@Transactional
	public void deleteQueueToken(QueueTokenDto dto) {
		tokenRepository.deleteByIdAndUserId(dto.tokenId(), dto.userId());
	}
}
