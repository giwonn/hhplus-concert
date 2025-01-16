package kr.hhplus.be.server.base;

import kr.hhplus.be.server.api.token.application.TokenService;
import kr.hhplus.be.server.api.token.application.port.out.QueueTokenResult;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class BaseControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@MockitoBean
	protected TokenService tokenService;

	@BeforeEach
	void setUp() {
		when(tokenService.checkQueuePassedAndUpdateToken(any())).thenReturn(new QueueTokenResult(1L, 1L, 0, null));
	}
}
