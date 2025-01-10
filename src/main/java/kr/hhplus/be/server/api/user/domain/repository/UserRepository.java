package kr.hhplus.be.server.api.user.domain.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.api.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT u FROM User u WHERE u.id = :userId")
	Optional<User> findByIdWithLock(@Param("userId") long userId);
}
