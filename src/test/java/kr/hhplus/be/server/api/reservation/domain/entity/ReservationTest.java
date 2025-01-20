package kr.hhplus.be.server.api.reservation.domain.entity;

import kr.hhplus.be.server.api.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.provider.FixedTimeProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

	@Nested
	class 예약_확정 {
		@Test
		void 대기중인_예약_확정() {
			// given
			Reservation reservation = new Reservation(1L, 1L, 1000L, ReservationStatus.WAITING, null, null);

			// when
			reservation.confirm(Instant.now());

			// then
			assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
			assertThat(reservation.getPaidAt()).isNotNull();
		}

		@Test
		void 만료된_예약은_확정처리_할_수_없다() {
			// given
			Reservation reservation = new Reservation(1L, 1L, 1000L, ReservationStatus.EXPIRED, null, null);

			// when & then
			assertThatThrownBy(() -> reservation.confirm(FixedTimeProvider.FIXED_TIME))
					.hasMessage(ReservationErrorCode.NOT_WAITING_RESERVATION.getReason());
		}

		@Test
		void 이미_확정된_예약은_확정처리_할_수_없다() {
			// given
			Reservation reservation = new Reservation(1L, 1L, 1000L, ReservationStatus.CONFIRMED, null, null);

			// when & then
			assertThatThrownBy(() -> reservation.confirm(FixedTimeProvider.FIXED_TIME))
					.hasMessage(ReservationErrorCode.NOT_WAITING_RESERVATION.getReason());
		}
	}
}
