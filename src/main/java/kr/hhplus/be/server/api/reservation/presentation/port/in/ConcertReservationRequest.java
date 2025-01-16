package kr.hhplus.be.server.api.reservation.presentation.port.in;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;

import java.util.Date;

public record ConcertReservationRequest(
		@NotNull
		Long concertId,

		@Min(1)
		@Max(50)
		@NotNull
		Long seatId,

		@NotNull
		Long amount,

		@NotNull
		Date date
) {
	public CreateReservationDto toDto() {
		return new CreateReservationDto(concertId, seatId, amount, date);
	}
}
