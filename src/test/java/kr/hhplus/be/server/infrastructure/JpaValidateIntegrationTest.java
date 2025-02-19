package kr.hhplus.be.server.infrastructure;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JpaValidateIntegrationTest {

	@Test
	void validateEntityMappings() {
		// 테스트 통과하면 DDL과 JPA 엔티티 매핑 성공
	}
}
