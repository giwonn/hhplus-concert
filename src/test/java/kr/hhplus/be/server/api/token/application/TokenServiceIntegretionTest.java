package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.api.token.application.port.in.ValidateQueueTokenDto;
import kr.hhplus.be.server.api.token.domain.entity.TestTokenFactory;
import kr.hhplus.be.server.api.token.domain.entity.Token;
import kr.hhplus.be.server.api.token.domain.repository.TokenRepository;
import kr.hhplus.be.server.base.BaseIntegretionTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import kr.hhplus.be.server.api.token.application.port.in.SignQueueTokenDto;
import kr.hhplus.be.server.api.token.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.common.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(FixedClockBean.class)
class TokenServiceIntegretionTest extends BaseIntegretionTest {

	@Autowired
	TokenService tokenService;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	TimeProvider timeProvider;

	@BeforeEach
	protected void setUp() {
		for (long i = 1L; i <= 9L; i++) {
			// 4번까지는 대기열 통과 상태로 저장
			Token createToken = TestTokenFactory.create(
					i,i < 5L, timeProvider.now().plusSeconds(i < 5L ? Token.ACTIVATE_SECONDS : Token.WAIT_SECONDS));
			tokenRepository.save(createToken);
		}
	}

	@Nested
	class 토큰_발급 {
		@Test
		void 성공() {
			// given
			SignQueueTokenDto dto = new SignQueueTokenDto(10L);

			// when
			QueueTokenResult sut = tokenService.signQueueToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut).isNotNull();
				assertThat(sut.id()).isEqualTo(10L);
				assertThat(sut.waitingNumber()).isEqualTo(10L);
			});
		}
	}

	@Nested
	class 토큰_대기시간_갱신_및_검증 {

		@Test
		void 토큰_검증만_성공() {
			// given
			Token token = tokenRepository.findById(9L).get();
			ValidateQueueTokenDto dto = new ValidateQueueTokenDto(token.getId(), token.getUserId());

			// when
			QueueTokenResult sut = tokenService.checkQueuePassedAndUpdateToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut).isNotNull();
				assertThat(sut.id()).isEqualTo(token.getId());
				assertThat(sut.expiredAt()).isEqualTo(token.getExpiredAt());
			});
		}

		@Test
		void 토큰_대기시간_갱신과_검증_성공() {
			// given
			Token renewTarget = TestTokenFactory.create( 10L, false, timeProvider.now().plusSeconds(Token.WAIT_THRESHOLD_SECONDS - 1));
			tokenRepository.save(renewTarget);
			Token token = tokenRepository.findById(renewTarget.getId()).get();
			ValidateQueueTokenDto dto = new ValidateQueueTokenDto(token.getId(), token.getUserId());

			// when
			QueueTokenResult sut = tokenService.checkQueuePassedAndUpdateToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut).isNotNull();
				assertThat(sut.id()).isEqualTo(token.getId());
				assertThat(sut.userId()).isEqualTo(token.getUserId());
				assertThat(sut.expiredAt()).isEqualTo(timeProvider.now().plusSeconds(Token.WAIT_SECONDS));
			});
		}
	}

	@Nested
	class 대기열_토큰_활성화 {
		@Test
		void 테스트환경_기준_대기열은_최대_5명까지_활성화된다() {
			// given
			assertThat(tokenRepository.findById(5L).get().isQueuePassed()).isFalse();
			assertThat(tokenRepository.findById(6L).get().isQueuePassed()).isFalse();

			// when
			tokenService.activateQueueToken();

			// then
			assertThat(tokenRepository.findById(5L).get().isQueuePassed()).isTrue();
			assertThat(tokenRepository.findById(6L).get().isQueuePassed()).isFalse();
		}
	}

	@Nested
	class 대기열_토큰_삭제 {
		@Test
		void 테스트환경_기준_대기열이_만료된_토큰은_1번에_1명만_삭제된다() {
			// given
			Token expiredToken = TestTokenFactory.create( 10L, false, timeProvider.now().minusSeconds(1));
			tokenRepository.save(expiredToken);
			Token expiredToken2 = TestTokenFactory.create( 11L, false, timeProvider.now().minusSeconds(1));
			tokenRepository.save(expiredToken2);

			// when
			tokenService.deleteExpiredQueueToken();

			// then
			assertThat(tokenRepository.findById(10L)).isNotPresent();
			assertThat(tokenRepository.findById(11L)).isPresent();
		}
	}

}
