package kr.hhplus.be.server.api.queue.infrastructure;

import kr.hhplus.be.server.api.queue.domain.entity.ActiveToken;
import kr.hhplus.be.server.api.queue.domain.entity.WaitingToken;
import kr.hhplus.be.server.base.BaseIntegrationTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import kr.hhplus.be.server.core.provider.TimeProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Import(FixedClockBean.class)
public class QueueRedisRepositoryIntegrationTest extends BaseIntegrationTest {

	@Autowired
	QueueRedisRepository queueRedisRepository;

	@Autowired
	RedisTemplate<String, Long> redisTemplate;

	@Autowired
	TimeProvider timeProvider;

	@Nested
	class 대기열_토큰_추가 {

		@Test
		void 성공() {
			// when
			queueRedisRepository.pushToWaitingQueue(1L);

			// then
			Long rank = redisTemplate.opsForZSet().rank(queueRedisRepository.WAITING_QUEUE_KEY, 1L);
			assertThat(rank).isNotNull();
		}
	}

	@Nested
	class 대기중인_토큰_조회 {

		@Test
		void 성공() {
			// given
			redisTemplate.opsForZSet().add(queueRedisRepository.WAITING_QUEUE_KEY, 1L, 1);

			// when
			Optional<WaitingToken> token = queueRedisRepository.getWaitingToken(1L);

			// then
			assertThat(token).isPresent();
			assertThat(token.get().getUserId()).isEqualTo(1L);
			assertThat(token.get().getWaitingNumber()).isEqualTo(1L);
		}
	}

	@Nested
	class 대기중인_유저ID_리스트_조회 {

		@Test
		void 성공() {
			// given
			redisTemplate.opsForZSet().add(queueRedisRepository.WAITING_QUEUE_KEY, 1L, 1);
			redisTemplate.opsForZSet().add(queueRedisRepository.WAITING_QUEUE_KEY, 2L, 2);
			redisTemplate.opsForZSet().add(queueRedisRepository.WAITING_QUEUE_KEY, 3L, 3);

			// when
			List<Long> token = queueRedisRepository.getWaitingUserIds(2);

			// then
			assertThat(token).hasSize(2);
			assertThat(token.get(0)).isEqualTo(1L);
			assertThat(token.get(1)).isEqualTo(2L);
		}
	}

	@Nested
	class 대기중인_토큰_삭제 {

		@Test
		void 성공() {
			// given
			List<Long> userIds = List.of(1L, 2L, 3L);
			userIds.forEach(userId -> {
				redisTemplate.opsForZSet().add(queueRedisRepository.WAITING_QUEUE_KEY, userId, userId.intValue());
			});

			// when
			queueRedisRepository.removeWaitingTokens(userIds);

			// then
			Set<Long> waitingUserIds = redisTemplate.opsForZSet().range(queueRedisRepository.WAITING_QUEUE_KEY, 0, -1);
			assertThat(waitingUserIds).isEmpty();
		}
	}

	@Nested
	class 활성화_대기열에_유저_리스트_추가 {

		@Test
		void 성공() {
			// given
			List<Long> userIds = List.of(1L, 2L, 3L);

			// when
			queueRedisRepository.pushToActiveQueue(userIds, timeProvider.now());

			// then
			Set<Long> activeUserIds = redisTemplate.opsForZSet().range(queueRedisRepository.ACTIVE_QUEUE_KEY, 0, -1);

			assertThat(activeUserIds)
					.hasSize(userIds.size())
					.containsAll(userIds);
		}
	}

	@Nested
	class 특정_유저의_활성화된_토큰_조회 {

		@Test
		void 성공() {
			// given
			redisTemplate.opsForZSet().add(queueRedisRepository.ACTIVE_QUEUE_KEY, 1L, 1);

			// when
			Optional<ActiveToken> sut = queueRedisRepository.getActiveToken(1L);

			// then
			assertThat(sut).isPresent();
			assertThat(sut.get().getUserId()).isEqualTo(1L);
		}
	}

	@Nested
	class 만료된_활성화_토큰_제거 {

		@Test
		void 성공() {
			// given
			redisTemplate.opsForZSet().add(queueRedisRepository.ACTIVE_QUEUE_KEY, 1L, timeProvider.now().minusSeconds(1).getEpochSecond());
			redisTemplate.opsForZSet().add(queueRedisRepository.ACTIVE_QUEUE_KEY, 2L, timeProvider.now().getEpochSecond());

			// when
			queueRedisRepository.removeExpiredTokens();

			// then
			Set<Long> activeUserIds = redisTemplate.opsForZSet().range(queueRedisRepository.ACTIVE_QUEUE_KEY, 0, -1);

			assertThat(activeUserIds)
					.hasSize(1)
					.contains(2L);
		}
	}

	@Nested
	class 특정_유저의_활성화_토큰_제거 {

		@Test
		void 성공() {
			// given
			redisTemplate.opsForZSet().add(queueRedisRepository.ACTIVE_QUEUE_KEY, 1L, timeProvider.now().plusSeconds(1).getEpochSecond());
			redisTemplate.opsForZSet().add(queueRedisRepository.ACTIVE_QUEUE_KEY, 2L, timeProvider.now().plusSeconds(1).getEpochSecond());

			// when
			queueRedisRepository.removeActiveTokenByUserId(1L);

			// then
			Set<Long> activeUserIds = redisTemplate.opsForZSet().range(queueRedisRepository.ACTIVE_QUEUE_KEY, 0, -1);

			assertThat(activeUserIds)
					.hasSize(1)
					.contains(2L);

		}
	}




}
