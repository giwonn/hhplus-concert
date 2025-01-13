package kr.hhplus.be.server.api.user.application;

import kr.hhplus.be.server.api.user.application.port.in.ChargePointDto;
import kr.hhplus.be.server.api.user.application.port.in.UsePointDto;
import kr.hhplus.be.server.api.user.application.port.out.UserPointResult;
import kr.hhplus.be.server.api.user.domain.entity.TestUserFactory;
import kr.hhplus.be.server.api.user.domain.entity.User;
import kr.hhplus.be.server.api.user.domain.repository.UserPointHistoryRepository;
import kr.hhplus.be.server.api.user.domain.repository.UserRepository;
import kr.hhplus.be.server.api.user.exception.UserErrorCode;
import kr.hhplus.be.server.common.provider.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	UserService userService;

	@Mock
	UserPointHistoryRepository userPointHistoryRepository;

	@Mock
	UserRepository userRepository;

	TimeProvider timeProvider;

	@BeforeEach
	void setUp() {
		Clock clock = Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), Clock.systemUTC().getZone());
		timeProvider = new TimeProvider(clock);
		userService = new UserService(userRepository, userPointHistoryRepository, timeProvider);
	}

	@Nested
	class 유저_포인트_조회 {
		@Test
		void 성공() {
			// given
			long userId = 1L;
			User user = TestUserFactory.createMock(userId, 1000);
			when(userRepository.findById(userId)).thenReturn(Optional.of(user));

			// when
			UserPointResult sut = userService.getPointByUserId(userId);

			// then
			assertThat(sut.userId()).isEqualTo(user.getId());
			assertThat(sut.point()).isEqualTo(user.getPoint());
		}
		@Test
		void 실패_존재하지_않는_유저() {
			// given
			long userId = 1L;
			User user = TestUserFactory.createMock(userId, 1000);
			when(userRepository.findById(userId)).thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> userService.getPointByUserId(userId))
					.hasMessage(UserErrorCode.USER_NOT_FOUND.getReason());
		}
	}

	@Nested
	class 잔액_충전 {

		@Test
		void 성공() {
			// given
			long userId = 1L;
			when(userRepository.findByIdWithLock(userId)).thenReturn(Optional.of(TestUserFactory.createMock(userId, 1000)));
			when(userRepository.save(any(User.class))).thenReturn(TestUserFactory.createMock(userId, 2000));

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
			long userId = 1L;
			when(userRepository.findByIdWithLock(userId)).thenReturn(Optional.empty());

			ChargePointDto dto = new ChargePointDto(userId, 1000);

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
			when(userRepository.findByIdWithLock(userId)).thenReturn(Optional.of(TestUserFactory.createMock(userId, 1000)));
			when(userRepository.save(any(User.class))).thenReturn(TestUserFactory.createMock(userId, 0));

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
			long userId = 1L;
			when(userRepository.findByIdWithLock(userId)).thenReturn(Optional.empty());

			UsePointDto dto = new UsePointDto(userId, 1000);

			// when & then
			assertThatThrownBy(() -> userService.usePoint(dto))
					.hasMessage(UserErrorCode.USER_NOT_FOUND.getReason());
		}

		@Test
		void 실패_잔액부족() {
			// given
			long userId = 1L;
			when(userRepository.findByIdWithLock(userId)).thenReturn(Optional.of(TestUserFactory.createMock(userId, 0)));

			UsePointDto dto = new UsePointDto(userId, 1000);

			// when & then
			assertThatThrownBy(() -> userService.usePoint(dto))
					.hasMessage(UserErrorCode.NOT_ENOUGH_POINT.getReason());
		}
	}

}
