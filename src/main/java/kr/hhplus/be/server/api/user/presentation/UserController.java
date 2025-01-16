package kr.hhplus.be.server.api.user.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.user.application.UserService;
import kr.hhplus.be.server.api.user.application.port.in.UserPointDto;
import kr.hhplus.be.server.api.user.application.port.out.UserPointHistoryResult;
import kr.hhplus.be.server.api.user.application.port.out.UserPointResult;
import kr.hhplus.be.server.api.user.presentation.port.in.UserPointRequest;
import kr.hhplus.be.server.api.user.presentation.port.out.UserPointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserAPI {

	private final UserService userService;

	@GetMapping("/{userId}/points")
	public ResponseEntity<UserPointResponse> getPoint(@PathVariable("userId") long userId) {
		UserPointResult result = userService.getPointByUserId(userId);
		return ResponseEntity.ok(UserPointResponse.from(result));
	}

	@PostMapping("/points/charge")
	public ResponseEntity<UserPointResponse> charge(@Valid @RequestBody UserPointRequest request) {
		UserPointHistoryResult result = userService.chargePoint(new UserPointDto(request.userId(), request.amount()));
		return ResponseEntity.ok(new UserPointResponse(result.userId(), result.point()));
	}
}
