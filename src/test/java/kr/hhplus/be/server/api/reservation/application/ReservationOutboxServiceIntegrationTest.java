package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.reservation.domain.entity.ReservationOutbox;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationOutboxRepository;
import kr.hhplus.be.server.base.BaseIntegrationTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(FixedClockBean.class)
class ReservationOutboxServiceIntegrationTest extends BaseIntegrationTest {

	@Autowired
	ReservationOutboxRepository reservationOutboxRepository;

	@Autowired
	ReservationOutboxService reservationOutboxService;

	@Nested
	class 메시지_self_consume {
		@Test
		void 성공() {
			// given
			String reqeustId = UUID.randomUUID().toString();
			ReservationOutbox outbox = ReservationOutbox.of(
					reqeustId,
					"test-topic",
					"test-partition-key",
					"{ \"testKey\": \"testValue\" }"
			);
			reservationOutboxRepository.save(outbox);

			// when
			reservationOutboxService.updateOutboxPublished(outbox.getRequestId());

			// then
			Optional<ReservationOutbox> result = reservationOutboxRepository.findByRequestId(reqeustId);
			assertThat(result).isPresent();
			assertThat(result.get().getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
		}
	}
