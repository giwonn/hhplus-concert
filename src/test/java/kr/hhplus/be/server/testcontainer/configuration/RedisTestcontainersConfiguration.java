package kr.hhplus.be.server.testcontainer.configuration;

import com.redis.testcontainers.RedisContainer;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.utility.DockerImageName;

@Configuration
class RedisTestcontainersConfiguration {

	private static final RedisContainer REDIS_CONTAINER;

	static {
		REDIS_CONTAINER = new RedisContainer(DockerImageName.parse("redis:7.4.2"));
		REDIS_CONTAINER.start();

		System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
		System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getFirstMappedPort().toString());
	}

	@PreDestroy
	public void preDestroy() {
		if (REDIS_CONTAINER.isRunning()) {
			REDIS_CONTAINER.stop();
		}
	}
}
