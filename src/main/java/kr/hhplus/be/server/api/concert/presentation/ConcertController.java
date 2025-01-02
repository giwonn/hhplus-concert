package kr.hhplus.be.server.api.concert.presentation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.api.concert.presentation.dto.AvailableConcertSchedulesResponse;
import kr.hhplus.be.server.api.concert.presentation.dto.AvailableConcertSeatsResponse;
import kr.hhplus.be.server.api.concert.application.dto.ConcertSeatDto;
import kr.hhplus.be.server.api.concert.presentation.dto.ConcertReservationRequest;
import kr.hhplus.be.server.api.concert.presentation.dto.ConcertReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/concerts")
public class ConcertController {

	@GetMapping(path = "/{concertId}/available-dates")
	public ResponseEntity<AvailableConcertSchedulesResponse> availableDates(
			@PathVariable("concertId") long concertId
	) {
		AvailableConcertSchedulesResponse response = new AvailableConcertSchedulesResponse(
				1,
				List.of("2025-01-01", "2025-01-02", "2025-01-03", "2025-01-04")
		);
		return ResponseEntity.ok(response);
	}

	@GetMapping(path = "/{concertId}/schedules/{concertScheduleId}/available-seats")
	public ResponseEntity<AvailableConcertSeatsResponse> availableSeats(
			@PathVariable("concertId") long concertId,
			@PathVariable("concertScheduleId") long concertScheduleId
	) {
		AvailableConcertSeatsResponse response = new AvailableConcertSeatsResponse(
				List.of(
				new ConcertSeatDto(550, 1, 8000),
				new ConcertSeatDto(551, 2, 10000)
				)
		);
		return ResponseEntity.ok(response);
	}

	@PostMapping(path = "/reservation")
	public ResponseEntity<ConcertReservationResponse> reservation(@Valid @RequestBody ConcertReservationRequest request) {
		ConcertReservationResponse response = new ConcertReservationResponse(
				1,
				Instant.now().plus(5, ChronoUnit.MINUTES)
		);
		return ResponseEntity.ok(response);
	}


}
