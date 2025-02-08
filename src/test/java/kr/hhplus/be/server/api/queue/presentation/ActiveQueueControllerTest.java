package kr.hhplus.be.server.api.queue.presentation;

import kr.hhplus.be.server.api.queue.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.base.BaseControllerTest;
import kr.hhplus.be.server.core.exception.RequestErrorCode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QueueController.class)
public class ActiveQueueControllerTest extends BaseControllerTest {

	@Nested
	class 토큰_발급 {

		@Test
		void 성공() throws Exception {
			QueueTokenResult dto = new QueueTokenResult(
					1L,
					1L,
					null
			);
			when(queueService.signQueueToken(any())).thenReturn(dto);

			mockMvc.perform(post("/queue/tokens")
							.content("""
									{ "userId": 1 }
									""")
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.userId").value(1))
					.andExpect(jsonPath("$.waitingNumber").value(1))
					.andExpect(jsonPath("$.expiredAt").doesNotExist());
		}

		@Test
		void 실패_userId_누락() throws Exception {
			mockMvc.perform(post("/queue/tokens")
							.content("{}")
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.code").value(RequestErrorCode.INVALID_INPUT.getCode()));
		}
	}
}
