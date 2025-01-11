package kr.hhplus.be.server.base;

import kr.hhplus.be.server.util.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegretionTest {

	@Autowired
	DatabaseCleaner databaseCleaner;

	@BeforeEach
	final void baseSetUp() {
		databaseCleaner.clear();
	}

	protected void setUp() {
		// do nothing
	}
}
