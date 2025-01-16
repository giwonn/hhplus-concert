package kr.hhplus.be.server.api.token.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TokenTest {

	@Test
	void 만료시간이_10초미만_남았다면_isExpiringSoon은_true를_반환한다() {
		// given
		Instant now = Instant.now();
		Token token = TokenFixture.createMock(1L, 1L, false, now.plusSeconds(10));

		// when
		boolean result = token.isExpiringSoon(now.plusSeconds(9));

		// then
		assertThat(result).isTrue();
	}

	@Test
	void 만료시간이_정확히_10초이상_남았다면_isExpiringSoon은_false를_반환한다() {
		// given
		Instant now = Instant.now();
		Token token = TokenFixture.createMock(1L, 1L, false, now.plusSeconds(10));

		// when
		boolean result = token.isExpiringSoon(now.plusSeconds(10));

		// then
		assertThat(result).isTrue();
	}

	@Test
	void 만료시간이_정확히_10초_남았다면_isExpiringSoon은_false를_반환한다() {
		// given
		Instant now = Instant.now();
		Token token = TokenFixture.createMock(1L, 1L, false, now.plusSeconds(10));

		// when
		boolean result = token.isExpiringSoon(now.plusSeconds(11));

		// then
		assertThat(result).isTrue();
	}

	@Test
	void getWaitingNumber는_가장_오래된_대기중인_토큰을_받아_현재_토큰의_대기시간을_계산한다() {
		// given
		Token token = TokenFixture.createMock(10L, 1L, false, Instant.now());

		// when
		long result = token.getWaitingNumber(9L);

		// then
		assertThat(result).isEqualTo(2L);
	}
}
