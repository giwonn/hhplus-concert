package kr.hhplus.be.server.annotation.simplelock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleLock {
	String key();
	long expireTime() default 3L;
	TimeUnit timeUnit() default TimeUnit.SECONDS;
}
