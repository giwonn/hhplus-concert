package kr.hhplus.be.server.api.reservation.domain.repository;

import kr.hhplus.be.server.api.reservation.domain.entity.ReservationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationOutboxRepository extends JpaRepository<ReservationOutbox, Long> {
	Optional<ReservationOutbox> findByRequestId(String requestId);
}
