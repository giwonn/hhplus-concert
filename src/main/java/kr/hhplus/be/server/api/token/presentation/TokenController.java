package kr.hhplus.be.server.api.token.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.token.application.TokenService;
import kr.hhplus.be.server.api.token.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.api.token.presentation.port.in.SignQueueTokenRequest;
import kr.hhplus.be.server.api.token.presentation.port.out.QueueTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tokens")
public class TokenController implements TokenAPI {

	private final TokenService tokenService;

	@PostMapping("/concerts")
	public ResponseEntity<QueueTokenResponse> signQueueToken(@Valid @RequestBody final SignQueueTokenRequest req) {
		QueueTokenResult result = tokenService.signQueueToken(req.toDto());
		return ResponseEntity.ok(QueueTokenResponse.from(result));
	}

}
