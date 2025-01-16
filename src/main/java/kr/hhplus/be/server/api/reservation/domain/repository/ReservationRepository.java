package kr.hhplus.be.server.api.reservation.domain.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT r FROM Reservation r WHERE r.id = :id")
	Optional<Reservation> findByIdWithLock(long id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT r FROM Reservation r WHERE r.status = :status")
	List<Reservation> findByStatusWithLock(ReservationStatus status);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Reservation r SET r.status = :status WHERE r IN :reservations")
	int updateStatus(List<Reservation> reservations, ReservationStatus status);

	List<Reservation> findByConcertSeatIdAndStatus(long concertSeatId, ReservationStatus reservationStatus);
}
