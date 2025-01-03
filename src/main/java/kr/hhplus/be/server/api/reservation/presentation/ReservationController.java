package kr.hhplus.be.server.api.reservation.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.reservation.presentation.dto.ReservationPaymentRequest;
import kr.hhplus.be.server.api.reservation.presentation.dto.ReservationPaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

	@PostMapping("/payments")
	public ResponseEntity<ReservationPaymentResponse> payment(@Valid @RequestBody ReservationPaymentRequest reservation) {
		ReservationPaymentResponse response = new ReservationPaymentResponse(1, 8000);
		return ResponseEntity.ok(response);
	}
}
