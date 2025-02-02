package kr.hhplus.be.server.annotation.simplelock;

import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.RequestErrorCode;
import kr.hhplus.be.server.library.SpringELParser;
import kr.hhplus.be.server.provider.lock.LockProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;


@RequiredArgsConstructor
@Component
@Aspect
@Order(1)
public class SimpleLockAspect {

	private final LockProvider lockProvider;

	@Around("@annotation(simpleLock)")
	public Object simpleLock(ProceedingJoinPoint joinPoint, SimpleLock simpleLock) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		SimpleLock annotation = method.getAnnotation(SimpleLock.class);

		String key = annotation.resource().value + ":" +
				SpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), annotation.key());
		Optional<Boolean> lock = lockProvider.lock(key, 0, simpleLock.expireTime(), simpleLock.timeUnit());
		// lock이 false면 예외 발생시켜서 요청 거절
		if (lock.isPresent() && !lock.get()) throw new CustomException(RequestErrorCode.FAIL_REQUEST);

		try {
			// lock이 true거나 null이면 요청 수행
			return joinPoint.proceed();
		} finally {
			// lock이 true면 unlock 호출
			if (lock.isPresent()) lockProvider.unlock(simpleLock.key());
		}
	}
}
