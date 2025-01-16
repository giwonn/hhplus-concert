package kr.hhplus.be.server.api.reservation.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;
import kr.hhplus.be.server.api.reservation.application.port.in.ReservationPaymentDto;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationPaymentResult;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationResult;
import kr.hhplus.be.server.api.reservation.presentation.port.in.ConcertReservationRequest;
import kr.hhplus.be.server.api.concert.presentation.port.out.ReservationConcertResponse;
import kr.hhplus.be.server.api.reservation.presentation.port.in.ReservationPaymentRequest;
import kr.hhplus.be.server.api.reservation.presentation.port.out.ReservationPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController implements ReservationAPI {

	private final ReservationFacade reservationFacade;

	@PostMapping("/payments")
	public ResponseEntity<ReservationPaymentResponse> payment(@Valid @RequestBody ReservationPaymentRequest request) {
		ReservationPaymentDto requestDto = new ReservationPaymentDto(request.reservationId(), request.userId());
		ReservationPaymentResult result = reservationFacade.payment(requestDto);
		ReservationPaymentResponse response = new ReservationPaymentResponse(result.reservationId(), result.remainingPoint());
		return ResponseEntity.ok(response);
	}

	@PostMapping(path = "/concerts")
	public ResponseEntity<ReservationConcertResponse> reservation(@Valid @RequestBody ConcertReservationRequest request) {
		ReservationResult result = reservationFacade.reserve(request.toDto());
		return ResponseEntity.ok(ReservationConcertResponse.from(result));
	}
}
