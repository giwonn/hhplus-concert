package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.api.token.application.port.in.SignQueueTokenDto;
import kr.hhplus.be.server.api.token.application.port.in.QueueTokenDto;
import kr.hhplus.be.server.api.token.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.api.token.domain.entity.TokenFactory;
import kr.hhplus.be.server.api.token.domain.entity.TokenFixture;
import kr.hhplus.be.server.api.token.domain.entity.Token;
import kr.hhplus.be.server.api.token.domain.repository.TokenRepository;
import kr.hhplus.be.server.api.token.exception.TokenErrorCode;
import kr.hhplus.be.server.core.exception.CommonErrorCode;
import kr.hhplus.be.server.core.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

	TokenService tokenService;

	@Mock
	TokenFactory tokenFactory;

	TimeProvider timeProvider;

	@Mock
	TokenRepository tokenRepository;

	@Mock
	Environment environment;

	@BeforeEach
	void setUp() {
		Clock clock = Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), Clock.systemUTC().getZone());
		timeProvider = new TimeProvider(clock);
		tokenService = new TokenService(tokenRepository, tokenFactory, timeProvider, environment);
	}

	@Nested
	class 토큰_발급 {

		@Test
		void 대기열_토큰_발급에_성공한다() {
			// given
			Token token = TokenFixture.createMock( 2L, 2L, false, timeProvider.now());
			when(tokenFactory.createWaiting(2L)).thenReturn(token);
			when(tokenRepository.save(any(Token.class))).thenReturn(token);
			when(tokenRepository.findOldestWaitingTokenId()).thenReturn(Optional.of(1L));
			SignQueueTokenDto dto = new SignQueueTokenDto(2L);

			// when
			QueueTokenResult sut = tokenService.signQueueToken(dto);

			// then
			assertSoftly(softly -> {
				softly.assertThat(sut.id()).isEqualTo(token.getId());
				softly.assertThat(sut.userId()).isEqualTo(token.getUserId());
				softly.assertThat(sut.waitingNumber()).isEqualTo(2L);
			});
		}
	}

	@Nested
	class 토큰_검증_및_만료시간_갱신 {

		@Test
		void 대기열_대기중인_토큰의_만료시간이_임박하면_만료시간을_갱신해준다() {
			// given
			QueueTokenDto dto = new QueueTokenDto(1L, 2L);
			Token token = TokenFixture.createMock(1L, 2L, false, timeProvider.now().plusSeconds(Token.WAIT_THRESHOLD_SECONDS - 1));
			when(tokenRepository.findByIdAndUserIdWithLock(anyLong(), anyLong())).thenReturn(Optional.of(token));
			when(tokenRepository.findOldestWaitingTokenId()).thenReturn(Optional.of(1L));

			// when
			QueueTokenResult sut = tokenService.checkQueuePassedAndUpdateToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut.id()).isEqualTo(token.getId());
				assertThat(sut.userId()).isEqualTo(token.getUserId());
				assertThat(sut.expiredAt()).isEqualTo(timeProvider.now().plusSeconds(Token.WAIT_SECONDS));
				verify(tokenRepository, times(1)).save(token);
			});
		}

		@Test
		void 대기열_대기중인_토큰의_만료시간이_널널하면_만료시간은_갱신되지_않는다() {
			// given
			QueueTokenDto dto = new QueueTokenDto(1L, 2L);
			Token token = TokenFixture.createMock(1L, 2L, false, timeProvider.now().plusSeconds(Token.WAIT_THRESHOLD_SECONDS));
			when(tokenRepository.findByIdAndUserIdWithLock(anyLong(), anyLong())).thenReturn(Optional.of(token));
			when(tokenRepository.findOldestWaitingTokenId()).thenReturn(Optional.of(1L));

			// when
			QueueTokenResult sut = tokenService.checkQueuePassedAndUpdateToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut.id()).isEqualTo(token.getId());
				assertThat(sut.userId()).isEqualTo(token.getUserId());
				assertThat(sut.expiredAt()).isEqualTo(token.getExpiredAt());
				verify(tokenRepository, times(0)).save(token);
			});
		}

		@Test
		void 대기열_통과한_토큰의_만료시간이_임박해도_만료시간은_갱신되지_않는다() {
			// given
			QueueTokenDto dto = new QueueTokenDto(1L, 2L);
			Token token = TokenFixture.createMock(1L, 2L, true, timeProvider.now().plusSeconds(Token.WAIT_THRESHOLD_SECONDS - 1));
			when(tokenRepository.findByIdAndUserIdWithLock(anyLong(), anyLong())).thenReturn(Optional.of(token));
			when(tokenRepository.findOldestWaitingTokenId()).thenReturn(Optional.of(1L));

			// when
			QueueTokenResult sut = tokenService.checkQueuePassedAndUpdateToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut.id()).isEqualTo(token.getId());
				assertThat(sut.userId()).isEqualTo(token.getUserId());
				assertThat(sut.expiredAt()).isEqualTo(token.getExpiredAt());
				verify(tokenRepository, times(0)).save(token);
			});
		}

		@Test
		void 존재하지_않는_토큰으로_대기열_검증시_NOT_FOUND_QUEUE_예외가_발생한다() {
			// given
			QueueTokenDto dto = new QueueTokenDto(1L, 2L);
			when(tokenRepository.findByIdAndUserIdWithLock(anyLong(), anyLong())).thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> tokenService.checkQueuePassedAndUpdateToken(dto))
					.hasMessage(TokenErrorCode.NOT_FOUND_QUEUE.getReason());
		}

		@Test
		void 만료된_토큰으로_대기열_검증시_QUEUE_EXPIRED_예외가_발생한다() {
			// given
			QueueTokenDto dto = new QueueTokenDto(1L, 2L);

			Token token = TokenFixture.createMock(1L, 2L, false, timeProvider.now().minusSeconds(10));
			when(tokenRepository.findByIdAndUserIdWithLock(anyLong(), anyLong())).thenReturn(Optional.of(token));

			// when & then
			assertThatThrownBy(() -> tokenService.checkQueuePassedAndUpdateToken(dto))
					.hasMessage(TokenErrorCode.QUEUE_EXPIRED.getReason());
		}
	}

	@Nested
	class 대기열_토큰_활성화 {

		@Test
		void 성공() {
			// given
			List<Token> tokens = List.of(
					TokenFixture.createMock(1L, 6L, true, Instant.now()),
					TokenFixture.createMock(2L, 7L, true, Instant.now()),
					TokenFixture.createMock(3L, 8L, true, Instant.now()),
					TokenFixture.createMock(4L, 9L, false, Instant.now()),
					TokenFixture.createMock(5L, 10L, false, Instant.now())
			);
			when(environment.getProperty("queue.limit.active")).thenReturn("4");
			when(tokenRepository.findOldestTokensByDateAndLimit(any(), anyInt())).thenReturn(tokens.subList(0, 4));

			// when
			tokenService.activateQueueToken();

			// then
			verify(tokenRepository, times(1)).bulkActivateQueue(List.of(4L));
		}

		@Test
		void 실패_환경변수_queue_limit_active가_null이면_MISSING_ENV_예외_발생() {
			// given
			when(environment.getProperty("queue.limit.active")).thenReturn(null);

			// when & then
			assertThatThrownBy(() -> tokenService.activateQueueToken())
					.hasMessage(CommonErrorCode.MISSING_ENV.getReason());
		}
	}

	@Nested
	class 만료된_토큰_일괄_삭제_처리 {

		@Test
		void 성공() {
			// given
			List<Token> tokens = List.of(
					TokenFixture.createMock(1L, 6L, true, Instant.now()),
					TokenFixture.createMock(2L, 7L, true, Instant.now()),
					TokenFixture.createMock(3L, 8L, true, Instant.now()),
					TokenFixture.createMock(4L, 9L, false, Instant.now()),
					TokenFixture.createMock(5L, 10L, false, Instant.now())
			);
			when(environment.getProperty("queue.limit.active")).thenReturn("4");
			when(tokenRepository.findOldestTokensByDateAndLimit(any(), anyInt())).thenReturn(tokens.subList(0, 4));

			// when
			tokenService.activateQueueToken();

			// then
			verify(tokenRepository, times(1)).bulkActivateQueue(List.of(4L));
		}

		@Test
		void 실패_환경변수_queue_limit_delete가_null이면_MISSING_ENV_예외_발생() {
			// given
			when(environment.getProperty("queue.limit.delete")).thenReturn(null);

			// when & then
			assertThatThrownBy(() -> tokenService.deleteExpiredQueueTokens())
					.hasMessage(CommonErrorCode.MISSING_ENV.getReason());
		}
	}
}

