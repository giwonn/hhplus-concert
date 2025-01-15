package kr.hhplus.be.server.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class CustomException extends RuntimeException {

	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getReason());
		this.errorCode = errorCode;
	}

	public void log() {
		log.warn("[Exception] Code: {}, Reason: {}", getCode(), getReason(), this);
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
