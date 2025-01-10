package kr.hhplus.be.server.api.reservation.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.concert.presentation.port.out.ReservationConcertResponse;
import kr.hhplus.be.server.api.reservation.presentation.port.in.ConcertReservationRequest;
import kr.hhplus.be.server.api.reservation.presentation.port.in.ReservationPaymentRequest;
import kr.hhplus.be.server.api.reservation.presentation.port.out.ReservationPaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Reservation", description = "예약 API")
public interface ReservationControllerDocs {

	@Operation(summary = "예약 결제 요청", description = "예약 건에 대하여 결제를 요청합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "결제 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ReservationPaymentResponse.class)
					)
			)
	})
	ResponseEntity<ReservationPaymentResponse> payment(@RequestBody ReservationPaymentRequest request);

	@Operation(summary = "콘서트 좌석 예약", description = "콘서트 좌석 예약을 진행합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "대기열 토큰 반환 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ReservationConcertResponse.class)
					)
			)
	})
	ResponseEntity<ReservationConcertResponse> reservation(@RequestBody ConcertReservationRequest request);
}
