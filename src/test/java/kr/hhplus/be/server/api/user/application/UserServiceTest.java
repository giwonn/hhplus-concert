package kr.hhplus.be.server.api.user.application;

import kr.hhplus.be.server.api.user.application.port.in.UserPointDto;
import kr.hhplus.be.server.api.user.application.port.in.UserPointHistoryDto;
import kr.hhplus.be.server.api.user.application.port.out.UserPointHistoryResult;
import kr.hhplus.be.server.api.user.application.port.out.UserPointResult;
import kr.hhplus.be.server.api.user.domain.entity.*;
import kr.hhplus.be.server.api.user.domain.repository.UserRepository;
import kr.hhplus.be.server.api.user.domain.exception.UserErrorCode;
import kr.hhplus.be.server.provider.FixedTimeProvider;
import kr.hhplus.be.server.provider.TimeProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	UserService userService;

	@Mock
	UserRepository userRepository;

	@Mock
	TimeProvider timeProvider;

	@Nested
	class 유저_포인트_조회 {
		@Test
		void 성공() {
			// given
			long userId = 1L;
			User user = UserFixture.createMock(userId, 1000);
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
			when(userRepository.findById(userId)).thenReturn(Optional.of(UserFixture.createMock(userId, 3000)));
			when(timeProvider.now()).thenReturn(FixedTimeProvider.FIXED_TIME);

			UserPointDto dto = new UserPointDto(userId, 1000);

			// when
			UserPointHistoryResult sut = userService.chargePoint(dto);

			// then
			assertThat(sut.userId()).isEqualTo(userId);
			assertThat(sut.point()).isEqualTo(4000);
			assertThat(sut.transactionAt()).isEqualTo(FixedTimeProvider.FIXED_TIME);
		}

		@Test
		void 실패_존재하지_않는_유저() {
			// given
			long userId = 1L;
			when(userRepository.findById(userId)).thenReturn(Optional.empty());

			UserPointDto dto = new UserPointDto(userId, 1000);

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
			when(userRepository.findById(userId)).thenReturn(Optional.of(UserFixture.createMock(userId, 3000)));
			when(timeProvider.now()).thenReturn(FixedTimeProvider.FIXED_TIME);

			UserPointDto dto = new UserPointDto(userId, 1000);

			// when
			UserPointHistoryResult sut = userService.usePoint(dto);

			// then
			assertThat(sut.userId()).isEqualTo(userId);
			assertThat(sut.point()).isEqualTo(2000);
			assertThat(sut.transactionAt()).isEqualTo(FixedTimeProvider.FIXED_TIME);
		}

		@Test
		void 실패_존재하지_않는_유저() {
			// given
			long userId = 1L;
			when(userRepository.findById(userId)).thenReturn(Optional.empty());

			UserPointDto dto = new UserPointDto(userId, 1000);

			// when & then
			assertThatThrownBy(() -> userService.usePoint(dto))
					.hasMessage(UserErrorCode.USER_NOT_FOUND.getReason());
		}
	}

	@Nested
	class 잔액_롤백 {

		@Test
		void 성공() {
			// given
			long userId = 1L;
			when(userRepository.findByIdWithLock(userId)).thenReturn(Optional.of(UserFixture.createMock(userId, 3000)));

			UserPointHistoryDto dto = new UserPointHistoryDto(userId, 1000, FixedTimeProvider.FIXED_TIME);

			// when
			UserPointHistoryResult sut = userService.rollbackPoint(dto);

			// then
			assertThat(sut.userId()).isEqualTo(userId);
			assertThat(sut.point()).isEqualTo(4000);
			assertThat(sut.transactionAt()).isEqualTo(FixedTimeProvider.FIXED_TIME);
		}

		@Test
		void 실패_존재하지_않는_유저() {
			// given
			long userId = 1L;
			when(userRepository.findByIdWithLock(userId)).thenReturn(Optional.empty());

			UserPointHistoryDto dto = new UserPointHistoryDto(userId, 1000, FixedTimeProvider.FIXED_TIME);

			// when & then
			assertThatThrownBy(() -> userService.rollbackPoint(dto))
					.hasMessage(UserErrorCode.USER_NOT_FOUND.getReason());
		}
	}

}
