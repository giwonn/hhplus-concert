package kr.hhplus.be.server.api.user.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.api.user.domain.exception.UserErrorCode;
import kr.hhplus.be.server.exception.CustomException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	private Long id;

	@Getter
	private long point;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "userId")
	final List<UserPointHistory> userPointHistories = new ArrayList<>();

	@Version
	private Long version;


	User(long id, long point) {
		this.id = id;
		this.point = point;
	}

	User(long point) {
		this.point = point;
	}

	public void chargePoint(long point, Instant time) {
		this.point += point;
		userPointHistories.add(new UserPointHistory(this.id, UserPointAction.CHARGE, point, time));
	}

	public void usePoint(long point, Instant time) {
		if (this.point < point) throw new CustomException(UserErrorCode.NOT_ENOUGH_POINT);
		this.point -= point;
		userPointHistories.add(new UserPointHistory(this.id, UserPointAction.USE, -point, time));
	}

	public void rollbackPoint(long point, Instant time) {
		// 롤백할 포인트가 현재 포인트보다 많아도 일단 롤백
		this.point += point;
		userPointHistories.add(new UserPointHistory(this.id, UserPointAction.ROLLBACK, point, time));
	}
}
