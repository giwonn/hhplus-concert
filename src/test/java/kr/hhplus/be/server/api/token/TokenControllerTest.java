package kr.hhplus.be.server.api.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.token.presentation.TokenController;
import kr.hhplus.be.server.api.token.presentation.dto.TokenRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TokenController.class)
public class TokenControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Nested
	class 토큰_발급 {

		@Test
		void 성공() throws Exception {
			TokenRequest request = new TokenRequest(1, 1);
			mockMvc.perform(post("/tokens/concerts")
							.content(objectMapper.writeValueAsString(request))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(1))
					.andExpect(jsonPath("$.expiredAt").isNotEmpty());
		}
	}
}
