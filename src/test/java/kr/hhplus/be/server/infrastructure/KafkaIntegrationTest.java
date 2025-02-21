package kr.hhplus.be.server.infrastructure;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class KafkaIntegrationTest {

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	final String TOPIC = "test-topic";
	final CountDownLatch latch = new CountDownLatch(1);

	String receivedMessage;

	@KafkaListener(topics = TOPIC, groupId = "test-group")
	void listen(String message) {
		receivedMessage = message;
		latch.countDown();
	}

	@Test
	void producerConsumerTest() throws InterruptedException {
		// given
		String message = "kafka test message";

		// when
		kafkaTemplate.send(TOPIC, message);

		// then
		assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
		assertThat(receivedMessage).isEqualTo(message);
	}
}
