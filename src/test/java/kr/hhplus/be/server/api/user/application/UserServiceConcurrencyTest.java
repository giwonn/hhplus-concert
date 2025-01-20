package kr.hhplus.be.server.api.user.application;

import kr.hhplus.be.server.api.user.application.port.in.UserPointDto;
import kr.hhplus.be.server.api.user.domain.entity.User;
import kr.hhplus.be.server.api.user.domain.entity.UserFixture;
import kr.hhplus.be.server.api.user.domain.entity.UserPointHistory;
import kr.hhplus.be.server.api.user.domain.repository.UserPointHistoryRepository;
import kr.hhplus.be.server.api.user.domain.repository.UserRepository;
import kr.hhplus.be.server.base.BaseIntegrationTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import kr.hhplus.be.server.util.ConcurrencyTestUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@Import(FixedClockBean.class)
class UserServiceConcurrencyTest extends BaseIntegrationTest {

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserPointHistoryRepository userPointHistoryRepository;

	@Nested
	class 포인트_충전 {

		@Test
		void 열번의_충전중_성공한만큼_충전된다() throws InterruptedException {
			// given
			User user = UserFixture.create(0L);
			userRepository.save(user);

			UserPointDto dto = new UserPointDto(user.getId(), 1000L);

			// when
			List<Supplier<?>> tasks = new ArrayList<>();
			for (long i = 1; i <= 10; i++) {
				tasks.add(() -> userService.chargePoint(dto));
			}
			ConcurrencyTestUtil.Result result = ConcurrencyTestUtil.run(tasks);

			// then
			Optional<User> savedUser = userRepository.findById(user.getId());
			assertThat(savedUser).isPresent();
			assertThat(savedUser.get().getPoint()).isEqualTo(1000L * result.successCount());

			List<UserPointHistory> histories = userPointHistoryRepository.findByUserId(user.getId());
			assertThat(histories).hasSize(result.successCount());
		}

	}

}
