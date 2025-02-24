package kr.hhplus.be.server.util;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaCleaner {

	private final AdminClient adminClient;

	public KafkaCleaner(KafkaAdmin kafkaAdmin) {
		Properties properties = new Properties();
		properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAdmin.getConfigurationProperties().get("bootstrap.servers"));
		this.adminClient = AdminClient.create(properties);
	}

	public void clear() {
		try {
			// 1. 모든 토픽 조회
			ListTopicsResult listTopicsResult = adminClient.listTopics();
			Set<String> topics = listTopicsResult.names().get();
			if (topics.isEmpty()) return;

			// 2. 토픽 모두 삭제
			adminClient.deleteTopics(topics).all().get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException("Failed to reset Kafka topics", e);
		}
	}
}
