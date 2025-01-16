package kr.hhplus.be.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

	private final HttpStatus httpStatus;
	private final String code;
	private final String reason;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getReason());
		this.httpStatus = errorCode.getHttpStatus();
		this.code = errorCode.getCode();
		this.reason = errorCode.getReason();
	}

}
