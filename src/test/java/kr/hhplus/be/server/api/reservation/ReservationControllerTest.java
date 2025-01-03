package kr.hhplus.be.server.api.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.reservation.presentation.ReservationController;
import kr.hhplus.be.server.api.reservation.presentation.dto.ReservationPaymentRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {


	@Autowired
	private MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Nested
	class 예약_결제 {

		@Test
		void 예약_건의_결제를_성공한다() throws Exception {
			ReservationPaymentRequest request = new ReservationPaymentRequest(1, 1);
			mockMvc.perform(post("/reservation/payments")
							.content(objectMapper.writeValueAsString(request))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.reservationId").value(1))
					.andExpect(jsonPath("$.remainingPoint").value(8000));
		}
	}
}
