package kr.hhplus.be.server.provider;

import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.RequestErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class LockProvider {

	private final RedissonClient redissonClient;

	private final String LOCK_PREFIX = "lock:";

	public boolean lock(String key, long waitTime, long expireTime, TimeUnit timeUnit) {
		RLock lock = redissonClient.getLock(LOCK_PREFIX + key);
		try {
			return lock.tryLock(waitTime, expireTime, timeUnit);
		} catch (InterruptedException e) {
			throw new CustomException(RequestErrorCode.FAIL_REQUEST);
		}
	}

	public void unlock(String key) {
		RLock lock = redissonClient.getLock(LOCK_PREFIX + key);
		try {
			lock.unlock();
		} catch (IllegalMonitorStateException e) {
			log.warn("IllegalMonitorStateException - key: {}", key);
		}
	}

}
