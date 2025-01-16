package kr.hhplus.be.server.api.user.presentation;

import kr.hhplus.be.server.api.user.application.UserService;
import kr.hhplus.be.server.api.user.application.port.out.UserPointHistoryResult;
import kr.hhplus.be.server.api.user.application.port.out.UserPointResult;
import kr.hhplus.be.server.base.BaseControllerTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;

@WebMvcTest(UserController.class)
public class UserControllerTest extends BaseControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	UserService userService;

	@Nested
	class 포인트_조회 {

		@Test
		void 성공() throws Exception {
			when(userService.getPointByUserId(anyLong())).thenReturn(new UserPointResult(1L, 1000L));

			mockMvc.perform(get("/users/{userId}/points", 1)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.userId").value(1))
					.andExpect(jsonPath("$.point").value(1000L));
		}
	}

	@Nested
	class 포인트_충전 {

		@Test
		void 성공() throws Exception {
			when(userService.chargePoint(any()))
					.thenReturn(new UserPointHistoryResult(1L, 1000L, Instant.now()));

			mockMvc.perform(post("/users/points/charge")
							.content("""
								{ "userId": 1, "amount": 1000 }
							""")
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.userId").value(1))
					.andExpect(jsonPath("$.point").value(1000L));
		}
	}
}
