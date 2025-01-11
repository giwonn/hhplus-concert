package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReservationErrorCode implements ErrorCode {

	DUPLICATE_SEAT_RESERVATION(HttpStatus.NOT_FOUND, "동일한 좌석의 예약이 존재합니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "예약이 존재하지 않습니다.");

	private HttpStatus httpStatus;
	private String reason;

	ReservationErrorCode(HttpStatus httpStatus, String reason) {
		this.httpStatus = httpStatus;
		this.reason = reason;
	}
}
