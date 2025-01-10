package kr.hhplus.be.server.api.user.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long point;

	User(long id, long point) {
		this.id = id;
		this.point = point;
	}

	User(long point) {
		this.point = point;
	}

	public static User of(long point) {
		return new User(point);
	}

	static User of(long id, long point) {
		return new User(id, point);
	}

	public void chargePoint(long point) {
		this.point += point;
	}

	public void usePoint(long point) {
		this.point -= point;
	}
}
