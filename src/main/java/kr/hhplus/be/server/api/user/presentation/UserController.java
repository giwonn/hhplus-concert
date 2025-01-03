package kr.hhplus.be.server.api.user.presentation;
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
}
