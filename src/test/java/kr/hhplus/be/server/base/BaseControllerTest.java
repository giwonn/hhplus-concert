package kr.hhplus.be.server.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.queue.application.QueueService;
import kr.hhplus.be.server.api.queue.application.port.in.QueueTokenDto;
import kr.hhplus.be.server.api.queue.application.port.out.QueueTokenResult;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class BaseControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@MockitoBean
	protected QueueService queueService;
	protected QueueTokenDto tokenDto = new QueueTokenDto(1L);

	@Autowired
	protected ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		when(queueService.getActiveQueueToken(any())).thenReturn(Optional.of(new QueueTokenResult(1L, 0, null)));
	}
}
