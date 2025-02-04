package kr.hhplus.be.server.core.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	HttpStatus getHttpStatus();
	String getCode();
	String getReason();
}
