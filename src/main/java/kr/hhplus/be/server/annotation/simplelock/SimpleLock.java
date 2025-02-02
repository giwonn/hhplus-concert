package kr.hhplus.be.server.annotation.simplelock;

import kr.hhplus.be.server.provider.lock.LockResource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleLock {
	LockResource resource();
	String key();
	long expireTime() default 3L;
	TimeUnit timeUnit() default TimeUnit.SECONDS;
}

