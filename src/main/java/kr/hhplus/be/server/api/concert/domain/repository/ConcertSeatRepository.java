package kr.hhplus.be.server.api.concert.domain.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.api.concert.domain.entity.ConcertSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConcertSeatRepository extends JpaRepository<ConcertSeat, Long> {

	@Modifying(clearAutomatically = true)
	@Query("UPDATE ConcertSeat cs SET cs.isReserved = false WHERE cs.id IN :ids")
	void updateSeatReservableByIds(@Param("ids") List<Long> ids);

	List<ConcertSeat> findByConcertScheduleIdAndIsReservedFalse(long concertScheduleId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT c FROM ConcertSeat c WHERE c.id = :id")
	Optional<ConcertSeat> findByIdWithLock(@Param("id") long id);

	List<ConcertSeat> findByConcertScheduleId(long concertScheduleId);
}
