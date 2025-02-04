package kr.hhplus.be.server.core.provider.lock;

import kr.hhplus.be.server.core.exception.CustomException;
import kr.hhplus.be.server.core.exception.RequestErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class LockProvider {

	private final RedissonClient redissonClient;

	private final String LOCK_PREFIX = "lock";

	public Optional<Boolean> lock(String key, long waitTime, long expireTime, TimeUnit timeUnit) {
		try {
			RLock lock = redissonClient.getLock(LOCK_PREFIX + ":" + key);
			return Optional.of(lock.tryLock(waitTime, expireTime, timeUnit));
		} catch (InterruptedException e) {
			log.warn("Redis tryLock InterruptedException - key: {}", key);
			throw new CustomException(RequestErrorCode.FAIL_REQUEST);
		} catch (RedisException e) {
			log.warn("Redis tryLock {} - key: {}", e.getClass().getName(), key);
			return Optional.empty();
		}
	}

	public void unlock(String key) {
		try {
			RLock lock = redissonClient.getLock(LOCK_PREFIX + key);
			lock.unlock();
		} catch (IllegalMonitorStateException e) {
			log.warn("IllegalMonitorStateException - key: {}", key);
		}
	}

}
