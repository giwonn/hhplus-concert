package kr.hhplus.be.server.api.reservation.domain.repository;

import kr.hhplus.be.server.api.reservation.domain.entity.ReservationOutbox;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;
import kr.hhplus.be.server.core.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationOutboxRepository extends JpaRepository<ReservationOutbox, Long> {
	Optional<ReservationOutbox> findByRequestId(String requestId);
	List<ReservationOutbox> findByStatus(OutboxStatus status);
}
