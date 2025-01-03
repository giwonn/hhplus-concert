package kr.hhplus.be.server.api.user.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.user.presentation.dto.UserPointRequest;
import kr.hhplus.be.server.api.user.presentation.dto.UserPointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

	@GetMapping("/{userId}/points")
	public ResponseEntity<UserPointResponse> getPoint(@PathVariable("userId") long userId) {
		UserPointResponse response = new UserPointResponse(userId, 10000);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/points/charge")
	public ResponseEntity<UserPointResponse> charge(@Valid @RequestBody UserPointRequest request) {
		UserPointResponse response = new UserPointResponse(1, 10000);
		return ResponseEntity.ok(response);
	}
}
