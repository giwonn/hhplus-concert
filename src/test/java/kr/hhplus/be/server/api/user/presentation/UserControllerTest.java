package kr.hhplus.be.server.api.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.user.domain.entity.TestUserFactory;
import kr.hhplus.be.server.api.user.domain.repository.UserRepository;
import kr.hhplus.be.server.api.user.presentation.port.in.UserPointRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected UserRepository userRepository;

	@BeforeEach
	void setUp() {
		userRepository.save(TestUserFactory.create(10000));
	}

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

	@Nested
	class 포인트_충전 {

		@Test
		void 성공() throws Exception {
			UserPointRequest request = new UserPointRequest(1, 10000);
			mockMvc.perform(post("/users/points/charge")
							.content(objectMapper.writeValueAsString(request))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.userId").value(1))
					.andExpect(jsonPath("$.point").value(20000));
		}
	}
}
