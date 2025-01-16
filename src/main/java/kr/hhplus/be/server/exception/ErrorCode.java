package kr.hhplus.be.server.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	HttpStatus getHttpStatus();
	String getCode();
	String getReason();
}
