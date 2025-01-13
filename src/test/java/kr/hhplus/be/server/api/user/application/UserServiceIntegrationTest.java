package kr.hhplus.be.server.api.user.application;


import kr.hhplus.be.server.api.user.application.port.in.ChargePointDto;
import kr.hhplus.be.server.api.user.application.port.in.UsePointDto;
import kr.hhplus.be.server.api.user.application.port.out.UserPointResult;
import kr.hhplus.be.server.api.user.domain.entity.TestUserFactory;
import kr.hhplus.be.server.api.user.domain.entity.User;
import kr.hhplus.be.server.api.user.domain.repository.UserRepository;
import kr.hhplus.be.server.api.user.exception.UserErrorCode;
import kr.hhplus.be.server.base.BaseIntegretionTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(FixedClockBean.class)
public class UserServiceIntegrationTest extends BaseIntegretionTest {

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@Nested
	class 유저_포인트_조회 {
		@Test
		void 성공() {
			// given
			User user = TestUserFactory.create(1000);
			userRepository.save(user);

			// when
			UserPointResult sut = userService.getPointByUserId(1L);

			// then
			assertThat(sut.userId()).isEqualTo(1L);
			assertThat(sut.point()).isEqualTo(1000);
		}
		@Test
		void 실패_존재하지_않는_유저() {
			// given

			// when & then
			assertThatThrownBy(() -> userService.getPointByUserId(1L))
					.hasMessage(UserErrorCode.USER_NOT_FOUND.getReason());
		}
	}

	@Nested
	class 잔액_충전 {

		@Test
		void 성공() {
			// given
			long userId = 1L;
			User user = TestUserFactory.create(1000);
			userRepository.save(user);

			ChargePointDto dto = new ChargePointDto(userId, 1000);

			// when
			UserPointResult sut = userService.chargePoint(dto);

			// then
			assertThat(sut.userId()).isEqualTo(userId);
			assertThat(sut.point()).isEqualTo(2000);
		}

		@Test
		void 실패_존재하지_않는_유저() {
			// given
			ChargePointDto dto = new ChargePointDto(1L, 1000);

			// when & then
			assertThatThrownBy(() -> userService.chargePoint(dto))
					.hasMessage(UserErrorCode.USER_NOT_FOUND.getReason());
		}
	}

	@Nested
	class 잔액_사용 {

		@Test
		void 성공() {
			// given
			long userId = 1L;
			User user = TestUserFactory.create(1000);
			userRepository.save(user);

			UsePointDto dto = new UsePointDto(userId, 1000);

			// when
			UserPointResult sut = userService.usePoint(dto);

			// then
			assertThat(sut.userId()).isEqualTo(userId);
			assertThat(sut.point()).isZero();
		}

		@Test
		void 실패_존재하지_않는_유저() {
			// given
			ChargePointDto dto = new ChargePointDto(1L, 1000);

			// when & then
			assertThatThrownBy(() -> userService.chargePoint(dto))
					.hasMessage(UserErrorCode.USER_NOT_FOUND.getReason());
		}

		@Test
		void 실패_잔액부족() {
			// given
			User user = TestUserFactory.create(900);
			userRepository.save(user);

			UsePointDto dto = new UsePointDto(1L, 1000);

			// when & then
			assertThatThrownBy(() -> userService.usePoint(dto))
					.hasMessage(UserErrorCode.NOT_ENOUGH_POINT.getReason());
		}
	}
}
