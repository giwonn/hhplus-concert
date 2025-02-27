package kr.hhplus.be.server.api.reservation.presentation.port.in;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.api.reservation.application.port.in.CreateReservationDto;


public record ConcertReservationRequest(
		@NotNull
		Long userId,

		@NotNull
		Long seatId,

		@NotNull
		Long amount
) {
	public CreateReservationDto toDto() {
		return new CreateReservationDto(seatId, userId, amount);
	}
}
