package kr.hhplus.be.server.core.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse<Void>> handleException(Exception e) {
		log.warn("[Exception] Code: {}, Reason: {}", 500, e.getMessage(), e);
		return ResponseEntity.status(500).body(new ErrorResponse<>("500", e.getMessage(), null));
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse<Void>> handleCustomException(CustomException e) {
		log.warn("[CustomException] Code: {}, Reason: {}", e.getCode(), e.getReason());
		return ResponseEntity.status(e.getHttpStatus()).body(ErrorResponse.from(e));
	}

	// 존재하지 않는 API 요청시
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse<ErrorResponse.Detail>> handleNoHandlerFoundException() {
		return ResponseEntity.badRequest().body(ErrorResponse.from(RequestErrorCode.NOT_FOUND_API));
	}

	// 지원하지 않는 Method로 API 요청시
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse<ErrorResponse.Detail>> handleHttpRequestMethodNotSupportedException() {
		return ResponseEntity.badRequest().body(ErrorResponse.from(RequestErrorCode.UNSUPPORTED_API_METHOD));
	}

	// 필드 타입 불일치 예외
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse<ErrorResponse.Detail>> handleMethodArgumentTypeMismatchException() {
		return ResponseEntity.badRequest().body(ErrorResponse.from(RequestErrorCode.INVALID_INPUT));
	}

	// JSON 변환 예외
	@ExceptionHandler(HttpMessageConversionException.class)
	public ResponseEntity<ErrorResponse<ErrorResponse.Detail>> handleHttpMessageConversionException() {
		return ResponseEntity.badRequest().body(ErrorResponse.from(RequestErrorCode.INVALID_JSON));
	}

	// Validation 예외
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse<ErrorResponse.Detail>> handleConstraintViolationException() {
		return ResponseEntity.badRequest().body(ErrorResponse.from(RequestErrorCode.INVALID_INPUT));
	}

	// Request Path - path값 누락
	@ExceptionHandler(MissingPathVariableException.class)
	public ResponseEntity<ErrorResponse<ErrorResponse.Detail>> handleMissingPathVariableException() {
		return ResponseEntity.badRequest().body(ErrorResponse.from(RequestErrorCode.MISSING_PATH));
	}

	// Request Param - 매개변수 누락
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse<ErrorResponse.Detail>> handleMissingServletRequestParameterException(
			MissingServletRequestParameterException e
	) {
		List<ErrorResponse.Detail> details = List.of(new ErrorResponse.Detail(e.getParameterName(), e.getMessage()));
		return ResponseEntity.badRequest().body(ErrorResponse.from(RequestErrorCode.INVALID_INPUT, details));
	}

	// Request Body - Validation 예외
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<ErrorResponse.Detail>> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException e
	) {
		List<ErrorResponse.Detail> details = e.getBindingResult().getFieldErrors().stream()
				.map(error -> new ErrorResponse.Detail(error.getField(), error.getDefaultMessage()))
				.toList();
		return ResponseEntity.badRequest().body(ErrorResponse.from(RequestErrorCode.INVALID_INPUT, details));
	}

	@ExceptionHandler(OptimisticLockingFailureException.class)
	public ResponseEntity<ErrorResponse<ErrorResponse.Detail>> handleOptimisticLockException(OptimisticLockingFailureException e) {
		return ResponseEntity.status(RequestErrorCode.FAIL_REQUEST.getHttpStatus())
				.body(ErrorResponse.from(RequestErrorCode.FAIL_REQUEST, null));
	}
}
