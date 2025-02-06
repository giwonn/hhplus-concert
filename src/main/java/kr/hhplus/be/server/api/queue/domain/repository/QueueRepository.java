package kr.hhplus.be.server.api.queue.domain.repository;

import kr.hhplus.be.server.api.queue.domain.entity.ActiveToken;
import kr.hhplus.be.server.api.queue.domain.entity.WaitingToken;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface QueueRepository {

	Optional<WaitingToken> getWaitingToken(long userId);

	void pushToWaitingQueue(long userId);

	List<Long> getWaitingUserIds(int count);

	void removeWaitingTokens(List<Long> userIds);

	Optional<ActiveToken> getActiveToken(long userId);

	void pushToActiveQueue(List<Long> userIds, Instant expireTime);

	void removeExpiredTokens();

	void removeActiveTokenByUserId(long userId);
}
