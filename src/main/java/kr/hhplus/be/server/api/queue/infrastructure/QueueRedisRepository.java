package kr.hhplus.be.server.api.queue.infrastructure;

import kr.hhplus.be.server.api.queue.domain.entity.ActiveToken;
import kr.hhplus.be.server.api.queue.domain.entity.WaitingToken;
import kr.hhplus.be.server.api.queue.domain.repository.QueueRepository;
import kr.hhplus.be.server.core.provider.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class QueueRedisRepository implements QueueRepository {

	private final RedisTemplate<String, Long> redisTemplate;
	private final TimeProvider timeProvider;

	final String WAITING_QUEUE_KEY = "queue:waiting";
	final String ACTIVE_QUEUE_KEY = "queue:active";

	@Override
	public void pushToWaitingQueue(long userId) {
		redisTemplate.opsForZSet().add(WAITING_QUEUE_KEY, userId, timeProvider.now().getEpochSecond());
	}

	@Override
	public Optional<WaitingToken> getWaitingToken(long userId) {
		Long waitingNumber = redisTemplate.opsForZSet().rank(WAITING_QUEUE_KEY, userId);
		if (waitingNumber == null) return Optional.empty();
		return Optional.of(WaitingToken.of(userId, waitingNumber + 1L));
	}

	@Override
	public List<Long> getWaitingUserIds(int count) {
		Set<Long> userIds = redisTemplate.opsForZSet().range(WAITING_QUEUE_KEY, 0, count - 1L);
		if (userIds == null) return List.of();
		return userIds.stream().toList();
	}

	@Override
	public void removeWaitingTokens(List<Long> userIds) {
		redisTemplate.opsForZSet().remove(WAITING_QUEUE_KEY, userIds.toArray());
	}

	@Override
	public void pushToActiveQueue(List<Long> userIds, Instant expireTime) {
		RedisSerializer keySerializer = redisTemplate.getKeySerializer();
		RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
		redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			userIds.forEach(userId -> connection.zAdd(
					keySerializer.serialize(ACTIVE_QUEUE_KEY),
					expireTime.getEpochSecond(),
					valueSerializer.serialize(userId)
			));
			return null;
		});
	}

	@Override
	public Optional<ActiveToken> getActiveToken(long userId) {
		Double expiredTime = redisTemplate.opsForZSet().score(ACTIVE_QUEUE_KEY, userId);
		if (expiredTime == null) return Optional.empty();
		return Optional.of(ActiveToken.of(userId, expiredTime));
	}

	@Override
	public void removeExpiredTokens() {
		redisTemplate.opsForZSet().removeRangeByScore(ACTIVE_QUEUE_KEY, 0, timeProvider.now().minusSeconds(1).getEpochSecond());
	}

	@Override
	public void removeActiveTokenByUserId(long userId) {
		redisTemplate.opsForZSet().remove(ACTIVE_QUEUE_KEY, userId);
	}
}
