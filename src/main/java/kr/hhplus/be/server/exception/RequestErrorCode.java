package kr.hhplus.be.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RequestErrorCode implements ErrorCode {

	NOT_FOUND_API(HttpStatus.NOT_FOUND, "NOT_FOUND_API", "존재하지 않는 API입니다."),
	UNSUPPORTED_API_METHOD(HttpStatus.METHOD_NOT_ALLOWED, "UNSUPPORTED_API_METHOD", "지원하지 않는 API Method입니다."),
	MISSING_PATH(HttpStatus.BAD_REQUEST, "MISSING_PATH", "request path가 누락되었습니다."),
	INVALID_JSON(HttpStatus.BAD_REQUEST, "INVALID_JSON", "request body는 JSON 포맷이어야 합니다."),
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "입력값이 올바르지 않습니다."),
	FAIL_REQUEST(HttpStatus.BAD_REQUEST, "FAIL_REQUEST", "요청 실패");

	private HttpStatus httpStatus;
	private String code;
	private String reason;

	RequestErrorCode(HttpStatus httpStatus, String code, String reason) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.reason = reason;
	}

}
