package kr.hhplus.be.server.api.concert.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Concert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

}
