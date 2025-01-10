package kr.hhplus.be.server.api.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.user.presentation.port.in.UserPointRequest;
import kr.hhplus.be.server.api.user.presentation.port.out.UserPointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "User", description = "유저 API")
public interface UserControllerDocs {

	@Operation(summary = "유저 포인트 조회", description = "유저의 포인트를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "유저 포인트 조회 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = UserPointResponse.class)
					)
			)
	})
	ResponseEntity<UserPointResponse> getPoint(@PathVariable("userId") long userId);

	@Operation(summary = "유저 포인트 충전", description = "유저의 포인트를 충전합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "유저 포인트 충전 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = UserPointResponse.class)
					)
			)
	})
	ResponseEntity<UserPointResponse> charge(@RequestBody UserPointRequest request);
}
