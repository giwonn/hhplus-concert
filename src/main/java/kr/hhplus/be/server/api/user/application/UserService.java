package kr.hhplus.be.server.api.user.application;

import kr.hhplus.be.server.api.user.application.port.in.ChargePointDto;
import kr.hhplus.be.server.api.user.application.port.in.UsePointDto;
import kr.hhplus.be.server.api.user.application.port.out.UserPointResult;
import kr.hhplus.be.server.api.user.domain.entity.User;
import kr.hhplus.be.server.api.user.domain.entity.UserPointHistory;
import kr.hhplus.be.server.api.user.domain.repository.UserPointHistoryRepository;
import kr.hhplus.be.server.api.user.domain.repository.UserRepository;
import kr.hhplus.be.server.api.user.exception.UserErrorCode;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.provider.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final UserPointHistoryRepository userPointHistoryRepository;
	private final TimeProvider timeProvider;

	@Transactional(readOnly = true)
	public UserPointResult getPointByUserId(long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
		return UserPointResult.from(user);
	}

	@Transactional
	public UserPointResult chargePoint(ChargePointDto dto) {
		User user = userRepository.findByIdWithLock(dto.userId()).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
		user.chargePoint(dto.point());
		User updatedUser = userRepository.save(user);

		UserPointHistory chargeHistory = UserPointHistory.createChargeHistory(user.getId(), dto.point(), timeProvider.now());
		userPointHistoryRepository.save(chargeHistory);

		return UserPointResult.from(updatedUser);
	}

	@Transactional
	public UserPointResult usePoint(UsePointDto dto) {
		User user = userRepository.findByIdWithLock(dto.userId()).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

		if (user.getPoint() - dto.point() < 0) throw new CustomException(UserErrorCode.NOT_ENOUGH_POINT);
		user.usePoint(dto.point());
		User updatedUser = userRepository.save(user);

		UserPointHistory useHistory = UserPointHistory.createUseHistory(user.getId(), dto.point(), timeProvider.now());
		userPointHistoryRepository.save(useHistory);

		return UserPointResult.from(updatedUser);
	}
}
