package kr.hhplus.be.server.annotation.simplelock;

import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.RequestErrorCode;
import kr.hhplus.be.server.provider.LockProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Aspect
@Order(1)
public class SimpleLockAspect {

	private final LockProvider lockProvider;
	private final ExpressionParser parser = new SpelExpressionParser();

	@Around("@annotation(simpleLock)")
	public Object simpleLock(ProceedingJoinPoint joinPoint, SimpleLock simpleLock) throws Throwable {
		String lockKey = evaluateKey(joinPoint, simpleLock.key());
		boolean lock = lockProvider.lock(lockKey, 0, simpleLock.expireTime(), simpleLock.timeUnit());
		if (!lock) throw new CustomException(RequestErrorCode.FAIL_REQUEST);

		try {
			return joinPoint.proceed();
		} finally {
			lockProvider.unlock(simpleLock.key());
		}
	}

	private String evaluateKey(ProceedingJoinPoint joinPoint, String keyExpression) {
		String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
		Object[] args = joinPoint.getArgs();

		EvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < paramNames.length; i++) {
			context.setVariable(paramNames[i], args[i]); // 변수 이름과 값 매핑
		}

		return parser.parseExpression(keyExpression).getValue(context, String.class);
	}
}
