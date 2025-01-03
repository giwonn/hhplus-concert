package kr.hhplus.be.server.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.user.presentation.UserController;
import kr.hhplus.be.server.api.user.presentation.dto.UserPointRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Nested
	class 포인트_조회 {

		@Test
		void 성공() throws Exception {
			mockMvc.perform(get("/users/{userId}/points", 1)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.userId").value(1))
					.andExpect(jsonPath("$.point").value(10000));
		}
	}
}
