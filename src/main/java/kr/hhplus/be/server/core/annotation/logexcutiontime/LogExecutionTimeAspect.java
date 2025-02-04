package kr.hhplus.be.server.core.annotation.logexcutiontime;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
public class LogExecutionTimeAspect {

	@Around("@annotation(kr.hhplus.be.server.core.annotation.logexcutiontime.LogExecutionTime)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		boolean isSuccess = true;

		try {
			return joinPoint.proceed();
		} catch (Throwable e) {
			isSuccess = false;
			throw e;
		} finally {
			long endTime = System.currentTimeMillis();
			String methodName = joinPoint.getSignature().toShortString();
			log.info("Method [{}] {} - {} ms", methodName, isSuccess ? "SUCCESS" : "FAIL", (endTime - startTime));
		}
	}
}
