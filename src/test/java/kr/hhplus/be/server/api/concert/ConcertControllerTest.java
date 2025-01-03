package kr.hhplus.be.server.api.concert;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.concert.presentation.ConcertController;
import kr.hhplus.be.server.api.concert.presentation.dto.ConcertReservationRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ConcertController.class)
class ConcertControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Nested
	class 예매_가능_날짜_조회 {

		@Test
		void 성공() throws Exception {
			mockMvc.perform(get("/concerts/{concertId}/available-dates", 1)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.concertId").value(1))
					.andExpect(jsonPath("$.dates").isArray());
		}
	}


	@Nested
	class 예매_가능_좌석_조회 {

		@Test
		void 성공() throws Exception {
			mockMvc.perform(get("/concerts/{concertId}/schedules/{concertScheduleId}/available-seats", 1, 1)
							.queryParam("date", "2024-01-01")
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.seats").isArray())
					.andExpect(jsonPath("$.seats").isNotEmpty());
		}
	}

	@Nested
	class 좌석_예매 {

		@Test
		void 성공() throws Exception {
			ConcertReservationRequest request = new ConcertReservationRequest(1, 1, Date.valueOf("2024-01-01"));
			mockMvc.perform(post("/concerts/reservation")
							.content(objectMapper.writeValueAsString(request))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.reservationId").value(1))
					.andExpect(jsonPath("$.expireTime").isNotEmpty());
		}
	}
}
