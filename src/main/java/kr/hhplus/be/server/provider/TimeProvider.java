package kr.hhplus.be.server.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@RequiredArgsConstructor
@Component
public class TimeProvider {

	private final Clock clock;

	public Instant now() {
		return Instant.now(clock);
	}

}

