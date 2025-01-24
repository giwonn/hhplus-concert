package kr.hhplus.be.server.api.reservation.application.port.in;

import java.util.Date;

public record ReserveSeatDto(
		long seatId,
		long userId,
		Date date
) {
}
