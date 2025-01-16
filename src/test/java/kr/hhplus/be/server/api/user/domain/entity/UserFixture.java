package kr.hhplus.be.server.api.user.domain.entity;

public class UserFixture {

	public static User createMock(long id, long point) {
		return new User(id, point);
	}

	public static User create(long point) {
		return new User(point);
	}
}
