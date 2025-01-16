package kr.hhplus.be.server.api.user.domain.entity;

import kr.hhplus.be.server.api.user.domain.exception.UserErrorCode;
import kr.hhplus.be.server.common.provider.FixedTimeProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserTest {

	@Nested
	class 포인트_충전 {
		@Test
		void 성공() {
			// given
			User user = UserFixture.createMock(1L, 1000L);

			// when
			user.chargePoint(1000L, FixedTimeProvider.FIXED_TIME);

			// then
			assertThat(user.getPoint()).isEqualTo(2000);
			assertThat(user.userPointHistories).hasSize(1);

			UserPointHistory userPointHistory = user.userPointHistories.get(0);
			assertThat(userPointHistory.getUserId()).isEqualTo(user.getId());
			assertThat(userPointHistory.getAmount()).isEqualTo(1000);
			assertThat(userPointHistory.getAction()).isEqualTo(UserPointAction.CHARGE);
		}
	}

	@Nested
	class 포인트_사용 {
		@Test
		void 성공() {
			// given
			User user = UserFixture.createMock(1L, 1000L);

			// when
			user.usePoint(1000L, FixedTimeProvider.FIXED_TIME);

			// then
			assertThat(user.getPoint()).isZero();
			assertThat(user.userPointHistories).hasSize(1);

			UserPointHistory userPointHistory = user.userPointHistories.get(0);
			assertThat(userPointHistory.getUserId()).isEqualTo(user.getId());
			assertThat(userPointHistory.getAmount()).isEqualTo(-1000);
			assertThat(userPointHistory.getAction()).isEqualTo(UserPointAction.USE);
		}

		@Test
		void 실패_잔액_부족() {
			// given
			User user = UserFixture.createMock(1L, 1000L);

			// when & then
			assertThatThrownBy(() -> user.usePoint(1001L, FixedTimeProvider.FIXED_TIME))
					.hasMessage(UserErrorCode.NOT_ENOUGH_POINT.getReason());

			assertThat(user.getPoint()).isEqualTo(1000L);
			assertThat(user.userPointHistories).isEmpty();

		}
	}

	@Nested
	class 포인트_롤백 {
		@Test
		void 성공() {
			// given
			User user = UserFixture.createMock(1L, 1000L);

			// when
			user.rollbackPoint(1000L, FixedTimeProvider.FIXED_TIME);

			// then
			assertThat(user.getPoint()).isEqualTo(2000L);
			assertThat(user.userPointHistories).hasSize(1);

			UserPointHistory userPointHistory = user.userPointHistories.get(0);
			assertThat(userPointHistory.getUserId()).isEqualTo(user.getId());
			assertThat(userPointHistory.getAmount()).isEqualTo(1000);
			assertThat(userPointHistory.getAction()).isEqualTo(UserPointAction.ROLLBACK);
		}

		@Test
		void 잔액이_음수가_되어도_성공() {
			// given
			User user = UserFixture.createMock(1L, 1000L);

			// when
			user.rollbackPoint(-2000L, FixedTimeProvider.FIXED_TIME);

			// then
			assertThat(user.getPoint()).isEqualTo(-1000L);
			assertThat(user.userPointHistories).hasSize(1);

			UserPointHistory userPointHistory = user.userPointHistories.get(0);
			assertThat(userPointHistory.getUserId()).isEqualTo(user.getId());
			assertThat(userPointHistory.getAmount()).isEqualTo(-2000L);
			assertThat(userPointHistory.getAction()).isEqualTo(UserPointAction.ROLLBACK);
		}
	}
}
