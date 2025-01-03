package kr.hhplus.be.server.api.concert.presentation;
import kr.hhplus.be.server.api.concert.presentation.dto.AvailableConcertSchedulesResponse;
import kr.hhplus.be.server.api.concert.presentation.dto.AvailableConcertSeatsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
}
