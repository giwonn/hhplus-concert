package kr.hhplus.be.server.api.reservation.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.core.enums.OutboxStatus;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Table(
	indexes = { @Index(name = "idx_status", columnList = "status") },
	uniqueConstraints = { @UniqueConstraint(name = "uk_request_id", columnNames = "request_id") }
)
@Getter
@EntityListeners(AuditingEntityListener.class)
@Entity
public class ReservationOutbox {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String requestId;

	@Column(nullable = false)
	private String topic;

	private String partitionKey;

	@Lob
	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OutboxStatus status;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@LastModifiedDate
	private Instant updatedAt;

	public ReservationOutbox() {}

	private ReservationOutbox(String requestId, String topic, String partitionKey, String message, OutboxStatus status) {
		this.requestId = requestId;
		this.topic = topic;
		this.partitionKey = partitionKey;
		this.message = message;
		this.status = status;
	}

	public static ReservationOutbox of(String requestId, String topic, String partitionKey, String message) {
		return new ReservationOutbox(
				requestId,
				topic,
				partitionKey,
				message,
				OutboxStatus.PENDING
		);
	}

	@PrePersist
	private void onCreate() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}

	public void published() {
		status = OutboxStatus.PUBLISHED;
	}

}
