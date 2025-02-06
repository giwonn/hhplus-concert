package kr.hhplus.be.server.api.queue.application;

import kr.hhplus.be.server.api.queue.application.port.in.QueueTokenDto;
import kr.hhplus.be.server.api.queue.domain.repository.QueueRepository;
import kr.hhplus.be.server.base.BaseIntegrationTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import kr.hhplus.be.server.api.queue.application.port.in.SignQueueTokenDto;
import kr.hhplus.be.server.api.queue.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.core.provider.TimeProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(FixedClockBean.class)
class QueueServiceIntegrationTest extends BaseIntegrationTest {

	@Autowired
	QueueService queueService;

	@Autowired
	QueueRepository queueRepository;

	@Autowired
	TimeProvider timeProvider;

	@Nested
	class 토큰_발급 {
		@Test
		void 성공() {
			// given
			SignQueueTokenDto dto = new SignQueueTokenDto(2L);

			// when
			QueueTokenResult sut = queueService.signQueueToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut).isNotNull();
				assertThat(sut.userId()).isEqualTo(2L);
				assertThat(sut.waitingNumber()).isEqualTo(1L);
				assertThat(sut.expiredAt()).isNull();
			});
		}
	}

	@Nested
	class 활성화_토큰_조회 {

		@Test
		void 토큰_검증만_성공() {
			// given
			queueRepository.pushToActiveQueue(List.of(1L, 2L, 3L), timeProvider.now());
			QueueTokenDto dto = new QueueTokenDto(2L);

			// when
			Optional<QueueTokenResult> sut = queueService.getActiveQueueToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut).isPresent();
				assertThat(sut.get().userId()).isEqualTo(2L);
				assertThat(sut.get().waitingNumber()).isZero();
				assertThat(sut.get().expiredAt()).isEqualTo(timeProvider.now());
			});
		}
	}

	@Nested
	class 대기중인_토큰_조회 {

		@Test
		void 토큰_검증만_성공() {
			// given
			List<Long> userIds = List.of(1L, 2L, 3L);
			userIds.forEach(userId -> queueRepository.pushToWaitingQueue(userId));
			QueueTokenDto dto = new QueueTokenDto(2L);

			// when
			QueueTokenResult sut = queueService.getWaitingQueueToken(dto);

			// then
			assertAll(() -> {
				assertThat(sut.userId()).isEqualTo(2L);
				assertThat(sut.waitingNumber()).isEqualTo(2);
				assertThat(sut.expiredAt()).isNull();
			});
		}
	}

	@Nested
	class 대기열_토큰_활성화 {
		@Test
		void 테스트환경_기준_대기열_토큰은_최대_5명씩_활성화_된다() {
			// given
			List<Long> userIds = List.of(1L, 2L, 3L, 4L, 5L, 6L);
			userIds.forEach(userId -> queueRepository.pushToWaitingQueue(userId));

			// when
			queueService.activateWaitingTokens();

			// then
			List<Long> waitingUserIds = queueRepository.getWaitingUserIds(6);
			assertThat(waitingUserIds).hasSize(1).contains(6L);
			assertThat(queueRepository.getActiveToken(1L)).isPresent();
			assertThat(queueRepository.getActiveToken(2L)).isPresent();
			assertThat(queueRepository.getActiveToken(3L)).isPresent();
			assertThat(queueRepository.getActiveToken(4L)).isPresent();
			assertThat(queueRepository.getActiveToken(5L)).isPresent();
		}
	}

	@Nested
	class 만료된_대기열_토큰_일괄_삭제 {
		@Test
		void 성공() {
			// given
			queueRepository.pushToActiveQueue(List.of(1L, 2L), timeProvider.now().minusSeconds(1));

			// when
			queueService.removeExpiredQueueTokens();

			// then
			assertThat(queueRepository.getActiveToken(1L)).isEmpty();
			assertThat(queueRepository.getActiveToken(2L)).isEmpty();
		}

		@Test
		void 만료시간이_현재시간과_같거나_이후면_삭제되지_않는다() {
			// given
			queueRepository.pushToActiveQueue(List.of(1L), timeProvider.now());
			queueRepository.pushToActiveQueue(List.of(2L), timeProvider.now().plusSeconds(1));

			// when
			queueService.removeExpiredQueueTokens();

			// then
			assertThat(queueRepository.getActiveToken(1L)).isPresent();
			assertThat(queueRepository.getActiveToken(2L)).isPresent();
		}
	}

	@Nested
	class 대기열_토큰_단건_삭제 {
		@Test
		void 성공() {
			// given
			queueRepository.pushToActiveQueue(List.of(1L, 2L), timeProvider.now().plusSeconds(1));

			// when
			queueService.removeActiveTokenByUserId(1L);

			// then
			assertThat(queueRepository.getActiveToken(1L)).isEmpty();
			assertThat(queueRepository.getActiveToken(2L)).isPresent();
		}
	}

}
