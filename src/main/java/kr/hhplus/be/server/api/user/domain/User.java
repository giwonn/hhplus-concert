package kr.hhplus.be.server.api.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long point;

	private User(long point) {
		this.point = point;
	}

	public static User create(long point) {
		return new User(point);
	}

}
