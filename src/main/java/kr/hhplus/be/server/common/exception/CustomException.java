package kr.hhplus.be.server.common.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getReason());
		this.errorCode = errorCode;
	}

	public HttpStatus getHttpStatus() {
		return errorCode.getHttpStatus();
	}

	public String getCode() {
		return errorCode.getCode();
	}

	public String getReason() {
		return errorCode.getReason();
	}

}
