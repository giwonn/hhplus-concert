package kr.hhplus.be.server.util;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@Component
@ActiveProfiles("test")
public class DatabaseCleaner implements InitializingBean {

	private final List<String> tableNames = new ArrayList<>();

	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void findDatabaseTableNames() {
		List<String> tables = entityManager.createNativeQuery("SHOW TABLES").getResultList();
		tableNames.addAll(tables);
	}

	private void truncate() {
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
		for (String tableName: tableNames) {
			entityManager.createNativeQuery(String.format("TRUNCATE TABLE %s", tableName)).executeUpdate();
		}
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
	}

	@Override
	public void afterPropertiesSet() {}

	@Transactional
	public void clear() {
		entityManager.clear();
		truncate();
	}

}
