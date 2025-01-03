package kr.hhplus.be.server.api.concert.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Concert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

}
