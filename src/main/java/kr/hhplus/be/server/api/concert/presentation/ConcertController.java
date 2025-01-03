package kr.hhplus.be.server.api.concert.presentation;
import kr.hhplus.be.server.api.concert.presentation.dto.AvailableConcertSchedulesResponse;
import kr.hhplus.be.server.api.concert.presentation.dto.AvailableConcertSeatsResponse;
import kr.hhplus.be.server.api.concert.application.dto.ConcertSeatDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
			@PathVariable("concertScheduleId") long concertScheduleId,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
	) {
		AvailableConcertSeatsResponse response = new AvailableConcertSeatsResponse(
				List.of(
				new ConcertSeatDto(550, 1, 8000),
				new ConcertSeatDto(551, 2, 10000)
				)
		);
		return ResponseEntity.ok(response);
	}
}
