package kr.hhplus.be.server.api.token.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.token.presentation.port.in.SignQueueTokenRequest;
import kr.hhplus.be.server.api.token.presentation.port.out.QueueTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "Token", description = "토큰 API")
public interface TokenControllerDocs {

	@Operation(summary = "콘서트 좌석 예약 대기열 토큰 발급", description = "콘서트 좌석 예약에 필요한 대기열 토큰을 발급합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "대기열 토큰 반환 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = QueueTokenResponse.class)
					)
			)
	})
	ResponseEntity<QueueTokenResponse> signQueueToken(@RequestBody SignQueueTokenRequest signQueueTokenRequest);
}
