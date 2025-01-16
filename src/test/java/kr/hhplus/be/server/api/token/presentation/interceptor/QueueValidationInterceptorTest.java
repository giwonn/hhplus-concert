package kr.hhplus.be.server.api.token.presentation.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.concert.application.ConcertService;
import kr.hhplus.be.server.api.concert.presentation.ConcertController;
import kr.hhplus.be.server.api.token.application.TokenService;
import kr.hhplus.be.server.api.token.application.port.in.QueueTokenDto;
import kr.hhplus.be.server.api.token.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.api.token.exception.TokenErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConcertController.class)
class QueueValidationInterceptorTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	TokenService tokenService;

	@MockitoBean
	ConcertService concertService;

	@Test
	void 토큰이_없으면_NOT_FOUND_QUEUE_예외_발생() throws Exception {
		mockMvc.perform(get("/concerts/1/available-dates")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(TokenErrorCode.NOT_FOUND_QUEUE.getCode()));
	}

	@Test
	void 토큰_형식이_잘못되었으면_INVALID_QUEUE_예외_발생() throws Exception {
		QueueTokenResult dto = new QueueTokenResult(
				1L,
				1L,
				1,
				Instant.parse("2024-01-01T12:00:00Z")
		);
		when(tokenService.checkQueuePassedAndUpdateToken(any())).thenReturn(dto);

		mockMvc.perform(get("/concerts/1/available-dates")
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Waiting-Token", objectMapper.writeValueAsString("{ invalid: 1}"))
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(TokenErrorCode.INVALID_QUEUE.getCode()));
	}

	@Test
	void 대기번호가_0이_아니면_대기열_대기중이므로_토큰을_리턴하고_요청_거절() throws Exception {
		QueueTokenResult dto = new QueueTokenResult(
				1L,
				1L,
				1,
				Instant.parse("2024-01-01T12:00:00Z")
		);
		when(tokenService.checkQueuePassedAndUpdateToken(any())).thenReturn(dto);

		QueueTokenDto tokenDto = new QueueTokenDto(1L, 1L);

		mockMvc.perform(get("/concerts/1/available-dates")
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Waiting-Token", objectMapper.writeValueAsString(tokenDto))
				)
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.userId").value(1))
				.andExpect(jsonPath("$.waitingNumber").value(1))
				.andExpect(jsonPath("$.expiredAt").value("2024-01-01T12:00:00Z"));
	}

	@Test
	void 대기번호가_0이면_대기열_통과하고_요청을_수행함() throws Exception {
		QueueTokenResult dto = new QueueTokenResult(
				1L,
				1L,
				0,
				Instant.parse("2024-01-01T12:00:00Z")
		);
		when(tokenService.checkQueuePassedAndUpdateToken(any())).thenReturn(dto);
		when(concertService.getReservableSchedules(anyLong())).thenReturn(List.of());

		QueueTokenDto tokenDto = new QueueTokenDto(1L, 1L);

		mockMvc.perform(get("/concerts/1/available-dates")
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Waiting-Token", objectMapper.writeValueAsString(tokenDto))
				)
				.andExpect(status().isOk());
	}
}
