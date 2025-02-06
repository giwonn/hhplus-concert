package kr.hhplus.be.server.api.queue.application;

import kr.hhplus.be.server.api.queue.application.port.in.SignQueueTokenDto;
import kr.hhplus.be.server.api.queue.application.port.in.QueueTokenDto;
import kr.hhplus.be.server.api.queue.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.api.queue.domain.entity.ActiveToken;
import kr.hhplus.be.server.api.queue.domain.entity.TokenFixture;
import kr.hhplus.be.server.api.queue.domain.entity.WaitingToken;
import kr.hhplus.be.server.api.queue.domain.repository.QueueRepository;
import kr.hhplus.be.server.api.queue.exception.TokenErrorCode;
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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

	QueueService queueService;

	TimeProvider timeProvider;

	@Mock
	QueueRepository queueRepository;

	@Mock
	Environment env;

	@BeforeEach
	void setUp() {
		Clock clock = Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), Clock.systemUTC().getZone());
		timeProvider = new TimeProvider(clock);
		queueService = new QueueService(queueRepository, timeProvider, env);
	}

	@Nested
	class 토큰_발급 {

		@Test
		void 대기열_토큰_발급에_성공한다() {
			// given
			WaitingToken waitingToken = TokenFixture.createWaiting( 2L, 2L);
			when(queueRepository.getWaitingToken(2L)).thenReturn(Optional.of(waitingToken));
			SignQueueTokenDto dto = new SignQueueTokenDto(2L);

			// when
			QueueTokenResult sut = queueService.signQueueToken(dto);

			// then
			assertThat(sut).isNotNull();
			assertThat(sut.userId()).isEqualTo(2L);
			assertThat(sut.waitingNumber()).isEqualTo(2L);
			assertThat(sut.expiredAt()).isNull();
		}

		@Test
		void 대기열_토큰_발급에_실패한다() {
			// given
			when(queueRepository.getWaitingToken(anyLong())).thenReturn(Optional.empty());
			SignQueueTokenDto dto = new SignQueueTokenDto(2L);

			// when & then
			assertThatThrownBy(() -> queueService.signQueueToken(dto))
					.hasMessage(TokenErrorCode.NOT_FOUND_QUEUE.getReason());
		}
	}

	@Nested
	class 활성화_토큰_조회 {

		@Test
		void 성공() {
			// given
			ActiveToken activeToken = ActiveToken.of(1L, timeProvider.now());
			when(queueRepository.getActiveToken(1L)).thenReturn(Optional.of(activeToken));
			QueueTokenDto dto = new QueueTokenDto(1L);

			// when
			Optional<QueueTokenResult> sut = queueService.getActiveQueueToken(dto);

			// then
			assertThat(sut).isPresent();
			assertThat(sut.get().userId()).isEqualTo(1L);
			assertThat(sut.get().waitingNumber()).isZero();
			assertThat(sut.get().expiredAt()).isEqualTo(timeProvider.now());
		}

		@Test
		void 조회된_토큰이_없으면_Optional_Empty를_반환한다() {
			// given
			when(queueRepository.getActiveToken(1L)).thenReturn(Optional.empty());
			QueueTokenDto dto = new QueueTokenDto(1L);

			// when
			Optional<QueueTokenResult> sut = queueService.getActiveQueueToken(dto);

			// then
			assertThat(sut).isEmpty();
		}

		@Test
		void 만료된_토큰은_Optional_Empty를_반환한다() {
			// given
			ActiveToken activeToken = ActiveToken.of(1L, timeProvider.now().minusMillis(1));
			when(queueRepository.getActiveToken(1L)).thenReturn(Optional.of(activeToken));
			QueueTokenDto dto = new QueueTokenDto(1L);

			// when
			Optional<QueueTokenResult> sut = queueService.getActiveQueueToken(dto);

			// then
			assertThat(sut).isEmpty();
		}
	}

	@Nested
	class 대기중인_토큰_조회 {

		@Test
		void 성공() {
			// given
			WaitingToken waitingToken = WaitingToken.of(1L, 2L);
			when(queueRepository.getWaitingToken(1L)).thenReturn(Optional.of(waitingToken));
			QueueTokenDto dto = new QueueTokenDto(1L);

			// when
			QueueTokenResult sut = queueService.getWaitingQueueToken(dto);

			// then
			assertThat(sut.userId()).isEqualTo(1L);
			assertThat(sut.waitingNumber()).isEqualTo(2L);
			assertThat(sut.expiredAt()).isNull();
		}

		@Test
		void 조회된_토큰이_없으면_NOT_FOUND_QUEUE_예외를_반환한다() {
			// given
			when(queueRepository.getWaitingToken(1L)).thenReturn(Optional.empty());
			QueueTokenDto dto = new QueueTokenDto(1L);

			// when & then
			assertThatThrownBy(() -> queueService.getWaitingQueueToken(dto))
					.hasMessage(TokenErrorCode.NOT_FOUND_QUEUE.getReason());
		}
	}

	@Nested
	class 대기중인_토큰_활성화 {

		@Test
		void 성공() {
			// given
			List<Long> userIds = List.of(1L, 2L);
			when(env.getProperty("queue.limit.active")).thenReturn("100");
			when(queueRepository.getWaitingUserIds(anyInt())).thenReturn(userIds);

			// when
			queueService.activateWaitingTokens();

			// then
			verify(queueRepository, times(1)).getWaitingUserIds(100);
			verify(queueRepository, times(1)).pushToActiveQueue(anyList(), any());
			verify(queueRepository, times(1)).removeWaitingTokens(anyList());
		}

		@Test
		void 실패_limit이_null_이면_MISSING_ENV_예외를_발생한다() {
			// given
			when(env.getProperty("queue.limit.active")).thenReturn(null);

			// when & then
			assertThatThrownBy(() -> queueService.activateWaitingTokens())
					.hasMessage(CommonErrorCode.MISSING_ENV.getReason());
		}

		@Test
		void 실패_대기중인_유저가_없다면_조기_종료한다() {
			// given
			when(env.getProperty("queue.limit.active")).thenReturn("100");
			when(queueRepository.getWaitingUserIds(anyInt())).thenReturn(List.of());

			// when
			queueService.activateWaitingTokens();

			// then
			verify(queueRepository, times(1)).getWaitingUserIds(100);
			verify(queueRepository, times(0)).pushToActiveQueue(any(), any());
			verify(queueRepository, times(0)).removeWaitingTokens(any());
		}
	}

	@Nested
	class 만료된_토큰_일괄_삭제_처리 {

		@Test
		void 성공() {
			// when
			queueService.removeExpiredQueueTokens();

			// then
			verify(queueRepository, times(1)).removeExpiredTokens();
		}
	}

	@Nested
	class 특정_유저의_토큰_삭제_처리 {

		@Test
		void 성공() {
			// when
			queueService.removeActiveTokenByUserId(1L);

			// then
			verify(queueRepository, times(1)).removeActiveTokenByUserId(1L);
		}
	}
}

