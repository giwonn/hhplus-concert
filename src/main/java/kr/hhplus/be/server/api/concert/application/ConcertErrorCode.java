package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ConcertErrorCode implements ErrorCode {

	NOT_FOUND_CONCERT(HttpStatus.BAD_REQUEST, "존재하지 않는 콘서트입니다."),
	NOT_FOUND_SCHEDULE(HttpStatus.BAD_REQUEST, "존재하지 않는 콘서트 스케쥴입니다."),
	NOT_FOUND_SEAT(HttpStatus.BAD_REQUEST, "존재하지 않는 콘서트 좌석입니다."),
	ALREADY_RESERVED_SEAT(HttpStatus.BAD_REQUEST, "이미 예약된 콘서트 좌석입니다.");

	private HttpStatus httpStatus;
	private String reason;

	ConcertErrorCode(HttpStatus httpStatus, String reason) {
		this.httpStatus = httpStatus;
		this.reason = reason;
	}

}
