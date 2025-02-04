package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.api.token.application.port.in.QueueTokenDto;
import kr.hhplus.be.server.api.token.domain.entity.TokenFactory;
import kr.hhplus.be.server.api.token.domain.entity.Token;
import kr.hhplus.be.server.api.token.domain.entity.TokenFixture;
import kr.hhplus.be.server.api.token.domain.repository.TokenRepository;
import kr.hhplus.be.server.base.BaseIntegrationTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import kr.hhplus.be.server.api.token.application.port.in.SignQueueTokenDto;
import kr.hhplus.be.server.api.token.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.core.provider.TimeProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(FixedClockBean.class)
class TokenServiceIntegrationTest extends BaseIntegrationTest {

	@Autowired
	TokenService tokenService;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	TimeProvider timeProvider;

	@Autowired
	TokenFactory tokenFactory;

	@Nested
	class 토큰_발급 {
		@Test
		void 성공() {
			// given
			tokenRepository.save(tokenFactory.createActivate(1L));
			SignQueueTokenDto dto = new SignQueueTokenDto(2L);

			// when
			QueueTokenResult sut = tokenService.signQueueToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut).isNotNull();
				assertThat(sut.id()).isEqualTo(2L);
				assertThat(sut.waitingNumber()).isEqualTo(1L);
			});
		}
	}

	@Nested
	class 토큰_대기시간_갱신_및_검증 {

		@Test
		void 토큰_검증만_성공() {
			// given
			tokenRepository.saveAll(List.of(
					tokenFactory.createActivate(10L),
					tokenFactory.createWaiting(20L),
					tokenFactory.createWaiting(30L)
			));
			QueueTokenDto dto = new QueueTokenDto(3L, 30L);

			// when
			QueueTokenResult sut = tokenService.checkQueuePassedAndUpdateToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut).isNotNull();
				assertThat(sut.id()).isEqualTo(3L);
				assertThat(sut.waitingNumber()).isEqualTo(2);
			});
		}

		@Test
		void 토큰_만료시간이_WAIT_THRESHOLD_SECONDS_미만이면_대기시간_갱신_및_검증_성공() {
			// given
			Token renewTarget = TokenFixture.create(
					1L,
					false,
					timeProvider.now().plusSeconds(Token.WAIT_THRESHOLD_SECONDS - 1)
			);
			tokenRepository.save(renewTarget);
			QueueTokenDto dto = new QueueTokenDto(renewTarget.getId(), renewTarget.getUserId());

			// when
			QueueTokenResult sut = tokenService.checkQueuePassedAndUpdateToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut).isNotNull();
				assertThat(sut.id()).isEqualTo(renewTarget.getId());
				assertThat(sut.userId()).isEqualTo(renewTarget.getUserId());
				assertThat(sut.expiredAt()).isEqualTo(timeProvider.now().plusSeconds(Token.WAIT_SECONDS));
			});
		}

		@Test
		void 토큰_만료시간이_WAIT_THRESHOLD_SECONDS_이상이면_대기시간_갱신안됨() {
			// given
			Token renewTarget = TokenFixture.create(
					1L,
					false,
					timeProvider.now().plusSeconds(Token.WAIT_THRESHOLD_SECONDS)
			);
			tokenRepository.save(renewTarget);
			QueueTokenDto dto = new QueueTokenDto(renewTarget.getId(), renewTarget.getUserId());

			// when
			QueueTokenResult sut = tokenService.checkQueuePassedAndUpdateToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut).isNotNull();
				assertThat(sut.id()).isEqualTo(renewTarget.getId());
				assertThat(sut.userId()).isEqualTo(renewTarget.getUserId());
				assertThat(sut.expiredAt()).isEqualTo(renewTarget.getExpiredAt());
			});
		}
	}

	@Nested
	class 대기열_토큰_활성화 {
		@Test
		void 테스트환경_기준_대기열은_최대_5명까지_활성화된다() {
			// given
			for (long i = 1L; i <= 10L; i++) {
				tokenRepository.save(tokenFactory.createWaiting(i));
			}

			// when
			tokenService.activateQueueToken();

			// then
			assertThat(tokenRepository.findById(5L).get().isQueuePassed()).isTrue();
			assertThat(tokenRepository.findById(6L).get().isQueuePassed()).isFalse();
		}
	}

	@Nested
	class 만료된_대기열_토큰_일괄_삭제 {
		@Test
		void 테스트환경_기준_대기열이_만료된_토큰은_1번에_1명만_삭제된다() {
			// given
			Token expiredToken = tokenFactory.create( 1L, false, timeProvider.now().minusSeconds(1));
			tokenRepository.save(expiredToken);

			Token expiredToken2 = tokenFactory.create( 2L, false, timeProvider.now().minusSeconds(1));
			tokenRepository.save(expiredToken2);

			// when
			tokenService.deleteExpiredQueueTokens();

			// then
			assertThat(tokenRepository.findById(1L)).isNotPresent();
			assertThat(tokenRepository.findById(2L)).isPresent();
		}

		@Test
		void 만료시간이_현재시간과_일치하면_삭제되지_않는다() {
			// given
			Token token = tokenFactory.create( 1L, false, timeProvider.now());
			tokenRepository.save(token);

			// when
			tokenService.deleteExpiredQueueTokens();

			// then
			assertThat(tokenRepository.findById(1L)).isPresent();
		}
	}

	@Nested
	class 대기열_토큰_단건_삭제 {
		@Test
		void 성공() {
			// given
			Token token = tokenFactory.create( 2L, true, timeProvider.now().plusSeconds(1));
			tokenRepository.save(token);

			QueueTokenDto dto = new QueueTokenDto(1L, 2L);

			// when
			tokenService.deleteQueueToken(dto);

			// then
			assertThat(tokenRepository.findById(1L)).isNotPresent();
		}
	}

}
