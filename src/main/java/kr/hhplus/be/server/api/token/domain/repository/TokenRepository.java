package kr.hhplus.be.server.api.token.domain.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.api.token.domain.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

	Optional<Token> findByIdAndUserId(long id, long userId);

	// 만료 여부 상관없이 가장 먼저 대기중인 토큰을 찾는 쿼리
	@Query("SELECT MIN(id) FROM Token WHERE expiredAt >= :now")
	Optional<Long> findOldestWaitingTokenId();

	@Query(value = "SELECT * FROM token WHERE expired_at > :expiredAt ORDER BY id LIMIT :limit", nativeQuery = true)
	List<Token> findOldestTokensByDateAndLimit(@Param("expiredAt") Instant expiredAt, @Param("limit") int limit);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Token SET isQueuePassed = true WHERE id IN :tokenIds")
	void bulkActivateQueue(List<Long> tokenIds);

	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM token WHERE expired_at < :time LIMIT :limit", nativeQuery = true)
	void deleteExpiredTokens(@Param("time") Instant time, @Param("limit") int limit);

	@Modifying(clearAutomatically = true)
	@Query("DELETE Token WHERE id = :tokenId AND userId = :userId")
	void deleteByTokenIdAndUserId(@Param("id") Long tokenId, @Param("userId") Long userId);

}
