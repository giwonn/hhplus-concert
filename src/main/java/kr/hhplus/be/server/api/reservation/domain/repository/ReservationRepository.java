package kr.hhplus.be.server.api.reservation.domain.repository;

import kr.hhplus.be.server.api.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	List<Reservation> findByStatus(ReservationStatus status);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Reservation r SET r.status = :status WHERE r IN :reservations")
	int updateStatus(List<Reservation> reservations, ReservationStatus status);

	List<Reservation> findByConcertSeatIdAndStatus(long concertSeatId, ReservationStatus reservationStatus);
}
