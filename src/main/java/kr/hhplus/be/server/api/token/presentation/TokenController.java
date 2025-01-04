package kr.hhplus.be.server.api.token.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.token.presentation.dto.TokenRequest;
import kr.hhplus.be.server.api.token.presentation.dto.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/tokens")
public class TokenController {

	@PostMapping("/concerts")
	public ResponseEntity<TokenResponse> signConcertToken(@Valid @RequestBody final TokenRequest tokenRequest) {
		TokenResponse response = new TokenResponse(1, Instant.now().plus(5, ChronoUnit.MINUTES));
		return ResponseEntity.ok(response);
	}

}
