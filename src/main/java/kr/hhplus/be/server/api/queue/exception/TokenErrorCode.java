package kr.hhplus.be.server.api.queue.exception;

import kr.hhplus.be.server.core.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TokenErrorCode implements ErrorCode {

	NOT_FOUND_QUEUE(HttpStatus.BAD_REQUEST, "TOKEN-01", "대기열 토큰을 발급해주세요."),
	INVALID_QUEUE(HttpStatus.BAD_REQUEST, "TOKEN-02", "유효하지 않은 대기열 토큰입니다."),
	QUEUE_EXPIRED(HttpStatus.BAD_REQUEST, "TOKEN-03", "만료된 대기열 토큰입니다.");

	private HttpStatus httpStatus;
	private String code;
	private String reason;

	TokenErrorCode(HttpStatus httpStatus, String code, String reason) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.reason = reason;
	}

}
