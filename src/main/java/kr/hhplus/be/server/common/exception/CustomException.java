package kr.hhplus.be.server.common.exception;

public class CustomException extends RuntimeException {

	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getReason());
		this.errorCode = errorCode;
	}

}
