package kr.hhplus.be.server.api.concert.presentation;

import kr.hhplus.be.server.api.concert.application.ConcertService;
import kr.hhplus.be.server.api.concert.application.port.out.ConcertScheduleResult;
import kr.hhplus.be.server.base.BaseControllerTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ConcertController.class)
class ConcertControllerTest extends BaseControllerTest {

	@MockitoBean
	ConcertService concertService;

	@Nested
	class 예매_가능_날짜_조회 {

		@Test
		void 성공() throws Exception {
			ConcertScheduleResult result = new ConcertScheduleResult(1L, 1L, LocalDate.parse("2024-01-01"), false);
			when(concertService.getReservableSchedules(anyLong())).thenReturn(List.of(result));

			mockMvc.perform(get("/concerts/{concertId}/available-dates", 1)
							.header("X-Waiting-Token", objectMapper.writeValueAsString(tokenDto))
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.schedules").isArray());
		}
	}

	@Nested
	class 예매_가능_좌석_조회 {

		@Test
		void 성공() throws Exception {
			when(concertService.getReservableSeats(anyLong())).thenReturn(List.of());

			mockMvc.perform(get("/concerts/{concertId}/schedules/{concertScheduleId}/available-seats", 1, 1)
							.header("X-Waiting-Token", objectMapper.writeValueAsString(tokenDto))
							.queryParam("date", "2024-01-01")
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.seats").isArray());
		}
	}
}
