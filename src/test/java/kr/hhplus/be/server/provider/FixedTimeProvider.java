package kr.hhplus.be.server.provider;

import java.time.Clock;
import java.time.Instant;

public class FixedTimeProvider {
	public static final Instant FIXED_TIME = Instant.parse("2025-01-01T12:00:00Z");

	public static TimeProvider create() {
		return new TimeProvider(Clock.fixed(FIXED_TIME, Clock.systemUTC().getZone()));
	}

	public static TimeProvider create(String time) {
		return new TimeProvider(Clock.fixed(Instant.parse(time), Clock.systemUTC().getZone()));
	}
}
