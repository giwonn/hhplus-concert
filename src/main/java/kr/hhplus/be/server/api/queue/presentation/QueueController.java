package kr.hhplus.be.server.api.queue.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.queue.application.QueueService;
import kr.hhplus.be.server.api.queue.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.api.queue.presentation.port.in.SignQueueTokenRequest;
import kr.hhplus.be.server.api.queue.presentation.port.out.QueueTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/queue")
public class QueueController implements QueueAPI {

	private final QueueService queueService;

	@PostMapping("/tokens")
	public ResponseEntity<QueueTokenResponse> signQueueToken(@Valid @RequestBody final SignQueueTokenRequest req) {
		QueueTokenResult result = queueService.signQueueToken(req.toDto());
		return ResponseEntity.ok(QueueTokenResponse.from(result));
	}

}
