package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.reservation.domain.entity.ReservationOutbox;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationOutboxFixture;
import kr.hhplus.be.server.api.reservation.domain.producer.ReservationProducer;
import kr.hhplus.be.server.api.reservation.domain.repository.ReservationOutboxRepository;
import kr.hhplus.be.server.base.BaseIntegrationTest;
import kr.hhplus.be.server.bean.FixedClockBean;
import kr.hhplus.be.server.core.enums.OutboxStatus;
import kr.hhplus.be.server.core.provider.TimeProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import(FixedClockBean.class)
class ReservationOutboxServiceIntegrationTest extends BaseIntegrationTest {

	@Autowired
	ReservationOutboxRepository reservationOutboxRepository;

	@Autowired
	ReservationOutboxService reservationOutboxService;

	@Autowired
	TimeProvider timeProvider;

	@MockitoSpyBean
	ReservationProducer reservationProducer;

	@Nested
	class 메시지_self_consume {
		@Test
		void 성공() {
			// given
			String requestId = UUID.randomUUID().toString();
			ReservationOutbox outbox = ReservationOutbox.of(
					requestId,
					"test-topic",
					"test-partition-key",
					"{ \"testKey\": \"testValue\" }",
					timeProvider.now()
			);
			reservationOutboxRepository.save(outbox);

			// when
			reservationOutboxService.updateOutboxPublished(outbox.getRequestId());

			// then
			Optional<ReservationOutbox> result = reservationOutboxRepository.findByRequestId(requestId);
			assertThat(result).isPresent();
			assertThat(result.get().getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
		}
	}

	@Nested
	class 오분_초과한_메시지_재전송 {
		@Test
		void 성공() {
			// given
			for (int i = 0; i < 10; i++) {
				reservationOutboxRepository.save(
						ReservationOutboxFixture.create(timeProvider.now().minusSeconds(301))
				);
			}
			reservationOutboxRepository.save(
					ReservationOutboxFixture.create(timeProvider.now().minusSeconds(300))
			);

			// when
			reservationOutboxService.sendFailureMessages();

			// then
			verify(reservationProducer, times(10)).send(anyString(), anyString(), any());
		}
	}

}
