package kr.hhplus.be.server.api.user.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.user.application.UserService;
import kr.hhplus.be.server.api.user.application.port.in.ChargePointDto;
import kr.hhplus.be.server.api.user.application.port.out.UserPointResult;
import kr.hhplus.be.server.api.user.presentation.port.in.UserPointRequest;
import kr.hhplus.be.server.api.user.presentation.port.out.UserPointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserControllerDocs {

	private final UserService userService;

	@GetMapping("/{userId}/points")
	public ResponseEntity<UserPointResponse> getPoint(@PathVariable("userId") long userId) {
		UserPointResult result = userService.getPointByUserId(userId);
		UserPointResponse response = new UserPointResponse(result.userId(), result.point());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/points/charge")
	public ResponseEntity<UserPointResponse> charge(@Valid @RequestBody UserPointRequest request) {
		UserPointResult result = userService.chargePoint(new ChargePointDto(request.userId(), request.amount()));
		UserPointResponse response = new UserPointResponse(result.userId(), result.point());
		return ResponseEntity.ok(response);
	}
}
