package kr.hhplus.be.server.api.user.application;


import kr.hhplus.be.server.api.user.application.port.in.UserPointDto;
import kr.hhplus.be.server.api.user.application.port.in.UserPointHistoryDto;
import kr.hhplus.be.server.api.user.application.port.out.UserPointHistoryResult;
import kr.hhplus.be.server.api.user.application.port.out.UserPointResult;
import kr.hhplus.be.server.api.user.domain.entity.UserFixture;
import kr.hhplus.be.server.api.user.domain.entity.User;
import kr.hhplus.be.server.api.user.domain.entity.UserPointAction;
import kr.hhplus.be.server.api.user.domain.entity.UserPointHistory;
import kr.hhplus.be.server.api.user.domain.repository.UserPointHistoryRepository;
import kr.hhplus.be.server.api.user.domain.repository.UserRepository;
import kr.hhplus.be.server.base.BaseIntegrationTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;


import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(FixedClockBean.class)
public class UserServiceIntegrationTest extends BaseIntegrationTest {

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserPointHistoryRepository userPointHistoryRepository;

	@Nested
	class 유저_포인트_조회 {

		@Test
		void 성공() {
			// given
			User user = UserFixture.create(1000L);
			userRepository.save(user);

			// when
			UserPointResult sut = userService.getPointByUserId(1L);

			// then
			assertThat(sut.userId()).isEqualTo(1L);
			assertThat(sut.point()).isEqualTo(1000L);
		}
	}

	@Nested
	class 잔액_충전 {

		@Test
		void 성공() {
			// given
			long userId = 1L;
			User user = UserFixture.create(3000L);
			userRepository.save(user);

			UserPointDto dto = new UserPointDto(userId, 1000L);

			// when
			UserPointHistoryResult sut = userService.chargePoint(dto);

			// then
			assertThat(sut.userId()).isEqualTo(userId);
			assertThat(sut.point()).isEqualTo(4000L);

			List<UserPointHistory> userPointHistory = userPointHistoryRepository.findByUserId(userId);
			assertThat(userPointHistory.get(0).getUserId()).isEqualTo(userId);
			assertThat(userPointHistory.get(0).getAction()).isEqualTo(UserPointAction.CHARGE);
			assertThat(userPointHistory.get(0).getAmount()).isEqualTo(1000L);
		}
	}

	@Nested
	class 잔액_사용 {

		@Test
		void 성공() {
			// given
			long userId = 1L;
			User user = UserFixture.create(3000L);
			userRepository.save(user);

			UserPointDto dto = new UserPointDto(userId, 1000L);

			// when
			UserPointHistoryResult sut = userService.usePoint(dto);

			// then
			assertThat(sut.userId()).isEqualTo(userId);
			assertThat(sut.point()).isEqualTo(2000L);

			List<UserPointHistory> userPointHistory = userPointHistoryRepository.findByUserId(userId);
			assertThat(userPointHistory.get(0).getUserId()).isEqualTo(userId);
			assertThat(userPointHistory.get(0).getAction()).isEqualTo(UserPointAction.USE);
			assertThat(userPointHistory.get(0).getAmount()).isEqualTo(-1000L);

		}
	}

	@Nested
	class 잔액_롤백 {

		@Test
		void 성공() {
			// given
			long userId = 1L;
			User user = UserFixture.create(2000L);
			userRepository.save(user);

			UserPointHistoryDto dto = new UserPointHistoryDto(userId, -1000L, Instant.now());

			// when
			UserPointHistoryResult sut = userService.rollbackPoint(dto);

			// then
			assertThat(sut.userId()).isEqualTo(userId);
			assertThat(sut.point()).isEqualTo(1000L);

			List<UserPointHistory> userPointHistory = userPointHistoryRepository.findByUserId(userId);
			assertThat(userPointHistory.get(0).getUserId()).isEqualTo(userId);
			assertThat(userPointHistory.get(0).getAction()).isEqualTo(UserPointAction.ROLLBACK);
			assertThat(userPointHistory.get(0).getAmount()).isEqualTo(-1000L);
		}
	}
}
