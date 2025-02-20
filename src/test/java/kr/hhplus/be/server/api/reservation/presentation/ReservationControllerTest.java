package kr.hhplus.be.server.api.reservation.presentation;

import kr.hhplus.be.server.api.reservation.application.port.out.ReservationPaymentResult;
import kr.hhplus.be.server.api.reservation.application.port.out.ReservationResult;
import kr.hhplus.be.server.api.reservation.domain.entity.ReservationStatus;
import kr.hhplus.be.server.api.reservation.presentation.port.in.ConcertReservationRequest;
import kr.hhplus.be.server.api.reservation.presentation.port.in.ReservationPaymentRequest;
import kr.hhplus.be.server.base.BaseControllerTest;
import kr.hhplus.be.server.core.exception.RequestErrorCode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest extends BaseControllerTest {

	@MockitoBean
	ReservationFacade reservationFacade;

	@Nested
	class 예약_결제 {

		@Test
		void 성공() throws Exception {
			// given
			ReservationPaymentResult result = new ReservationPaymentResult(1, 8000);
			when(reservationFacade.payment(any())).thenReturn(result);

			ReservationPaymentRequest request = new ReservationPaymentRequest(1L, 1);

			// when & then
			mockMvc.perform(post("/reservation/payments")
							.content(objectMapper.writeValueAsString(request))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.reservationId").value(1))
					.andExpect(jsonPath("$.remainingPoint").value(8000));
		}
	}

	@Nested
	class 좌석_예약 {

		@Test
		void 좌석번호가_1일_경우_성공() throws Exception {
			// given
			ReservationResult result = new ReservationResult(
					1L, 1L, 1L, 1000L, ReservationStatus.WAITING, Instant.now(), null);
			when(reservationFacade.reserve(any())).thenReturn(result);

			ConcertReservationRequest request = new ConcertReservationRequest(
					1L, 1L, 1000L);

			// when & then
			mockMvc.perform(post("/reservation/concerts")
							.content(objectMapper.writeValueAsString(request))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.reservationId").value(1))
					.andExpect(jsonPath("$.expireTime").value(result.expiredAt().toString()));
		}

		@Test
		void 좌석번호가_50일_경우_성공() throws Exception {
			// given
			ReservationResult result = new ReservationResult(
					1L, 50L, 1L, 1000L, ReservationStatus.WAITING, Instant.now(), null);
			when(reservationFacade.reserve(any())).thenReturn(result);

			ConcertReservationRequest request = new ConcertReservationRequest(
					1L, 50L, 1000L);

			// when & then
			mockMvc.perform(post("/reservation/concerts")
							.content(objectMapper.writeValueAsString(request))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.reservationId").value(1))
					.andExpect(jsonPath("$.expireTime").value(result.expiredAt().toString()));
		}

		@Test
		void 좌석번호가_0일_경우_요청_실패() throws Exception {
			// given
			ConcertReservationRequest request = new ConcertReservationRequest(
					1L, 0L, 1000L);

			// when & then
			mockMvc.perform(post("/reservation/concerts")
							.content(objectMapper.writeValueAsString(request))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.code").value(RequestErrorCode.INVALID_INPUT.getCode()));
		}

		@Test
		void 좌석번호가_51일_경우_요청_실패() throws Exception {
			// given
			ConcertReservationRequest request = new ConcertReservationRequest(
					1L, 51L,1000L);

			// when & then
			mockMvc.perform(post("/reservation/concerts")
							.content(objectMapper.writeValueAsString(request))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.code").value(RequestErrorCode.INVALID_INPUT.getCode()));
		}
	}
}
