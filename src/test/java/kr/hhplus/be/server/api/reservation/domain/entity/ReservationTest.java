package kr.hhplus.be.server.api.reservation.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationTest {

	@Test
	void 결제시간_추가() {
		// given
		Reservation reservation = new Reservation(1L, 1L, 1000L, ReservationStatus.WAITING, null, null);
		Instant paymentTime = Instant.now();

		// when
		reservation.addPaymentTime(paymentTime);

		// then
		assertThat(reservation.getPaidAt()).isEqualTo(paymentTime);
	}
}
