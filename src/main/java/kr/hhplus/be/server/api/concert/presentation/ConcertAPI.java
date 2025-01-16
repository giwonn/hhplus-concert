package kr.hhplus.be.server.api.concert.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.concert.presentation.port.out.AvailableConcertSchedulesResponse;
import kr.hhplus.be.server.api.concert.presentation.port.out.AvailableConcertSeatsResponse;
import kr.hhplus.be.server.api.concert.presentation.port.out.ReservationConcertResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Tag(name = "Concert", description = "콘서트 API")
public interface ConcertAPI {

	@Operation(summary = "예약 가능 날짜 조회", description = "예약 가능한 콘서트 날짜를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "예약 가능 날짜 조회 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = AvailableConcertSchedulesResponse.class)
					)
			)
	})
	ResponseEntity<AvailableConcertSchedulesResponse> availableDates(@PathVariable("concertId") long concertId);

	@Operation(summary = "예약 가능 좌석 조회", description = "예약 가능한 콘서트 좌석을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "예약 가능 좌석 조회 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ReservationConcertResponse.class)
					)
			)
	})
	ResponseEntity<AvailableConcertSeatsResponse> availableSeats(
			@PathVariable("concertId") long concertId,
			@PathVariable("concertScheduleId") long concertScheduleId,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
	);
}
