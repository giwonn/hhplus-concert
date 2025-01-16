package kr.hhplus.be.server.api.concert.presentation;

import kr.hhplus.be.server.api.concert.application.ConcertService;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertScheduleResult;
import kr.hhplus.be.server.api.concert.presentation.port.out.AvailableConcertSchedulesResponse;
import kr.hhplus.be.server.api.concert.presentation.port.out.AvailableConcertSeatsResponse;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertSeatResult;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
public class ConcertController implements ConcertAPI {

	private final ConcertService concertService;

	@GetMapping(path = "/{concertId}/available-dates")
	public ResponseEntity<AvailableConcertSchedulesResponse> availableDates(
			@PathVariable("concertId") long concertId
	) {
		List<ConcertScheduleResult> result = concertService.getReservableSchedules(concertId);
		AvailableConcertSchedulesResponse response = AvailableConcertSchedulesResponse.from(result);
		return ResponseEntity.ok(response);
	}

	@GetMapping(path = "/{concertId}/schedules/{concertScheduleId}/available-seats")
	public ResponseEntity<AvailableConcertSeatsResponse> availableSeats(
			@PathVariable("concertId") long concertId,
			@PathVariable("concertScheduleId") long concertScheduleId,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
	) {
		List<ConcertSeatResult> result = concertService.getReservableSeats(concertScheduleId);
		return ResponseEntity.ok(new AvailableConcertSeatsResponse(result));
	}

}
