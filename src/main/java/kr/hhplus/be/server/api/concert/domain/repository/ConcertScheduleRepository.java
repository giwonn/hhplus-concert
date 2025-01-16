package kr.hhplus.be.server.api.concert.domain.repository;

import kr.hhplus.be.server.api.concert.domain.entity.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {

	List<ConcertSchedule> findByConcertId(long concertId);
}
