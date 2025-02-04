package kr.hhplus.be.server.api.user.application;

import kr.hhplus.be.server.api.user.application.port.in.UserPointDto;
import kr.hhplus.be.server.api.user.application.port.in.UserPointHistoryDto;
import kr.hhplus.be.server.api.user.application.port.out.UserPointHistoryResult;
import kr.hhplus.be.server.api.user.application.port.out.UserPointResult;
import kr.hhplus.be.server.api.user.domain.entity.User;
import kr.hhplus.be.server.api.user.domain.repository.UserRepository;
import kr.hhplus.be.server.api.user.domain.exception.UserErrorCode;
import kr.hhplus.be.server.core.exception.CustomException;
import kr.hhplus.be.server.core.provider.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final TimeProvider timeProvider;

	@Transactional(readOnly = true)
	public UserPointResult getPointByUserId(long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
		return UserPointResult.from(user);
	}

	@Transactional
	public UserPointHistoryResult chargePoint(UserPointDto dto) {
		User user = userRepository.findByIdWithLock(dto.userId()).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

		Instant transactionAt = timeProvider.now();
		user.chargePoint(dto.point(), transactionAt);

		return UserPointHistoryResult.of(user.getId(), user.getPoint(), transactionAt);
	}

	@Transactional
	public UserPointHistoryResult usePoint(UserPointDto dto) {
		User user = userRepository.findByIdWithLock(dto.userId()).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

		Instant transactionAt = timeProvider.now();
		user.usePoint(dto.point(), transactionAt);

		return UserPointHistoryResult.of(user.getId(), user.getPoint(), transactionAt);
	}

	@Transactional
	public UserPointHistoryResult rollbackPoint(UserPointHistoryDto dto) {
		User user = userRepository.findByIdWithLock(dto.userId()).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
		user.rollbackPoint(dto.point(), dto.transactionAt());
		if (user.getPoint() < 0) {
			log.warn("롤백은 성공하였으나, userId({})의 잔액이 부족합니다. 현재 포인트: {}", user.getId(), user.getPoint());
		}
		return UserPointHistoryResult.of(user.getId(), user.getPoint(), dto.transactionAt());
	}
}
