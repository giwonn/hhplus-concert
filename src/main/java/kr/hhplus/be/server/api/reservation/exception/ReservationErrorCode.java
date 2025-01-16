package kr.hhplus.be.server.api.reservation.exception;

import kr.hhplus.be.server.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReservationErrorCode implements ErrorCode {

	NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION-01", "예약이 존재하지 않습니다."),
	DUPLICATE_SEAT_RESERVATION(HttpStatus.NOT_FOUND, "RESERVATION-02", "동일한 좌석의 예약이 존재합니다.");

	private HttpStatus httpStatus;
	private String code;
	private String reason;

	ReservationErrorCode(HttpStatus httpStatus, String code, String reason) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.reason = reason;
	}
}
