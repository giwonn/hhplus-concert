package kr.hhplus.be.server.api.user.domain.exception;

import kr.hhplus.be.server.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {

	USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER-01", "존재하지 않는 유저입니다."),

	NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "USER-02", "포인트가 부족합니다."),
	DUPLICATE_POINT_REQUEST(HttpStatus.CONFLICT, "USER-03", "포인트 중복 요청입니다.");

	private HttpStatus httpStatus;
	private String code;
	private String reason;

	UserErrorCode(HttpStatus httpStatus, String code, String reason) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.reason = reason;
	}
}
