package kr.hhplus.be.server.api.user.domain.repository;

import kr.hhplus.be.server.api.user.domain.entity.UserPointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPointHistoryRepository extends JpaRepository<UserPointHistory, Long> {

	List<UserPointHistory> findByUserId(Long userId);
}
