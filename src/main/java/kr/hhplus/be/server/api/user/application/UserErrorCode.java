package kr.hhplus.be.server.api.user.application;

import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {

	USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "찾을 수 없는 유저입니다."),
	NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다.");

	private HttpStatus httpStatus;
	private String reason;

	UserErrorCode(HttpStatus httpStatus, String reason) {
		this.httpStatus = httpStatus;
		this.reason = reason;
	}
}
