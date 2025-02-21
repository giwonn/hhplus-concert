package kr.hhplus.be.server.api.reservation.application.port.in;

import java.time.LocalDateTime;

public record ReserveSeatDto(
		long seatId,
		long userId,
		long amount,
		LocalDateTime date
) {
}
