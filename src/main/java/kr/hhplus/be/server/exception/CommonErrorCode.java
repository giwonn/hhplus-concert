package kr.hhplus.be.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {

	MISSING_ENV(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-01", "Internal Server Error");

	private final HttpStatus httpStatus;
	private final String code;
	private final String reason;

	CommonErrorCode(HttpStatus httpStatus, String code, String reason) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.reason = reason;
	}

}
