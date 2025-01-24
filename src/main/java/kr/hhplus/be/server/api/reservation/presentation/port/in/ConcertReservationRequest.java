package kr.hhplus.be.server.api.reservation.presentation.port.in;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.api.reservation.application.port.in.ReserveSeatDto;

import java.util.Date;

public record ConcertReservationRequest(
		@NotNull
		Long userId,

		@Min(1)
		@Max(50)
		@NotNull
		Long seatId,

		@NotNull
		Date date
) {
	public ReserveSeatDto toDto() {
		return new ReserveSeatDto(seatId, userId, date);
	}
}
