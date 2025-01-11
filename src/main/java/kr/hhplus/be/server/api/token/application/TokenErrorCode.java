package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TokenErrorCode implements ErrorCode {

	NOT_FOUND_QUEUE(HttpStatus.BAD_REQUEST, "존재하지 않는 대기열 토큰입니다."),
	INVALID_QUEUE(HttpStatus.BAD_REQUEST, "유효하지 않은 대기열 토큰입니다."),
	QUEUE_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 대기열 토큰입니다.");

	private HttpStatus httpStatus;
	private String reason;

	TokenErrorCode(HttpStatus httpStatus, String reason) {
		this.httpStatus = httpStatus;
		this.reason = reason;
	}

}
