package kr.hhplus.be.server.common.exception;

public record ErrorResponse(
		String code,
		String reason
) {
	public static ErrorResponse from(CustomException e) {
		return new ErrorResponse(e.getCode(), e.getReason());
	}
}
