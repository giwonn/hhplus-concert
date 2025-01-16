package kr.hhplus.be.server.api.token.presentation;

import kr.hhplus.be.server.api.token.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.base.BaseControllerTest;
import kr.hhplus.be.server.exception.RequestErrorCode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TokenController.class)
public class TokenControllerTest extends BaseControllerTest {

	@Nested
	class 토큰_발급 {

		@Test
		void 성공() throws Exception {
			QueueTokenResult dto = new QueueTokenResult(
					1L,
					1L,
					1,
					Instant.parse("2024-01-01T12:00:00Z")
			);
			when(tokenService.signQueueToken(any())).thenReturn(dto);

			mockMvc.perform(post("/tokens/concerts")
							.content("""
									{ "userId": 1 }
									""")
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(1))
					.andExpect(jsonPath("$.userId").value(1))
					.andExpect(jsonPath("$.waitingNumber").value(1))
					.andExpect(jsonPath("$.expiredAt").value("2024-01-01T12:00:00Z"));
		}

		@Test
		void 실패_userId_누락() throws Exception {
			mockMvc.perform(post("/tokens/concerts")
							.content("{}")
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.code").value(RequestErrorCode.INVALID_INPUT.getCode()));
		}
	}
}
