package kr.hhplus.be.server.api.concert.exception;

import kr.hhplus.be.server.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ConcertErrorCode implements ErrorCode {

	NOT_FOUND_CONCERT(HttpStatus.BAD_REQUEST,  "CONCERT-01", "존재하지 않는 콘서트입니다."),
	NOT_FOUND_SEAT(HttpStatus.BAD_REQUEST, "CONCERT-02", "존재하지 않는 콘서트 좌석입니다."),
	NOT_FOUND_SCHEDULE(HttpStatus.BAD_REQUEST,  "CONCERT-03", "현재 예약 가능한 콘서트 스케쥴이 없습니다."),
	ALREADY_RESERVED_SEAT(HttpStatus.BAD_REQUEST, "CONCERT-04", "이미 예약된 콘서트 좌석입니다.");

	private HttpStatus httpStatus;
	private String code;
	private String reason;

	ConcertErrorCode(HttpStatus httpStatus, String code, String reason) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.reason = reason;
	}

}
