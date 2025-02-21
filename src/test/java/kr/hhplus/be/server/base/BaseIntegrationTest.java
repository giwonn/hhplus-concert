package kr.hhplus.be.server.base;

import kr.hhplus.be.server.util.DatabaseCleaner;
import kr.hhplus.be.server.util.KafkaCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

	@Autowired
	DatabaseCleaner databaseCleaner;

	@Autowired
	RedissonClient redissonClient;
	@Autowired
	private KafkaCleaner kafkaCleaner;

	@BeforeEach
	final void baseSetUp() {
		databaseCleaner.clear();
		redissonClient.getScript(StringCodec.INSTANCE).eval(
				RScript.Mode.READ_WRITE,
				"return redis.call('FLUSHALL')",
				RScript.ReturnType.VALUE
		);
		kafkaCleaner.clear();
	}
}
