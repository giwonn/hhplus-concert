package kr.hhplus.be.server.exception;

import lombok.Getter;

import java.util.List;


public record ErrorResponse<T>(
		String code,
		String reason,
		List<T> details
) {
	public static ErrorResponse<Void> from(CustomException e) {
		return new ErrorResponse<>(e.getCode(), e.getReason(), null);
	}

	public static ErrorResponse<Detail> from(ErrorCode errorCode) {
		return new ErrorResponse<>(errorCode.getCode(), errorCode.getReason(), null);
	}

	public static ErrorResponse<Detail> from(ErrorCode errorCode, List<Detail> details) {
		return new ErrorResponse<>(errorCode.getCode(), errorCode.getReason(), details);
	}

	@Getter
	public static class Detail {
		private final String field;
		private final String message;

		Detail(String field, String message) {
			this.field = field;
			this.message = message;
		}
	}

}
