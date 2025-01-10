package kr.hhplus.be.server.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class FixedClockBean {

	@Bean("test-clock")
	@Primary
	public static Clock clock(@Value("${fixedTime:2025-01-01T12:00:00Z}") String time) {
		return Clock.fixed(
				Instant.parse(time),
				ZoneId.of("UTC")
		);
	}
}
