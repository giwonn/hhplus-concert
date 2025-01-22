package kr.hhplus.be.server.annotation.simplelock;

import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.RequestErrorCode;
import kr.hhplus.be.server.provider.LockProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Aspect
@Order(1)
public class SimpleLockAspect {

	private final LockProvider lockProvider;

	@Around("@annotation(simpleLock)")
	public Object simpleLock(ProceedingJoinPoint joinPoint, SimpleLock simpleLock) throws Throwable {
		boolean lock = lockProvider.lock(simpleLock.key(), 0, simpleLock.expireTime(), simpleLock.timeUnit());
		if (!lock) throw new CustomException(RequestErrorCode.FAIL_REQUEST);

		try {
			return joinPoint.proceed();
		} finally {
			lockProvider.unlock(simpleLock.key());
		}
	}
}
