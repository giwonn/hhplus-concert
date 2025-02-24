package kr.hhplus.be.server.api.reservation.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.core.enums.OutboxStatus;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Table(
	indexes = { @Index(name = "idx_reservation_outbox_status", columnList = "status") },
	uniqueConstraints = { @UniqueConstraint(name = "uk_reservation_outbox_request_id", columnNames = "request_id") }
)
@Getter
@EntityListeners(AuditingEntityListener.class)
@Entity
public class ReservationOutbox {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
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

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@LastModifiedDate
	private Instant updatedAt;

	public ReservationOutbox() {}

	private ReservationOutbox(String requestId, String topic, String partitionKey, String message, OutboxStatus status, Instant createdAt) {
		this.requestId = requestId;
		this.topic = topic;
		this.partitionKey = partitionKey;
		this.message = message;
		this.status = status;
		this.createdAt = createdAt;
	}

	public static ReservationOutbox of(String requestId, String topic, String partitionKey, String message, Instant createdAt) {
		return new ReservationOutbox(
				requestId,
				topic,
				partitionKey,
				message,
				OutboxStatus.PENDING,
				createdAt
		);
	}

	public void published() {
		status = OutboxStatus.PUBLISHED;
	}

}
