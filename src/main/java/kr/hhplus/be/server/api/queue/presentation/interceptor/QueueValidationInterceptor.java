package kr.hhplus.be.server.api.queue.presentation.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.api.queue.application.port.in.QueueTokenDto;
import kr.hhplus.be.server.api.queue.exception.TokenErrorCode;
import kr.hhplus.be.server.api.queue.application.QueueService;
import kr.hhplus.be.server.api.queue.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.api.queue.presentation.port.out.QueueTokenResponse;
import kr.hhplus.be.server.core.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QueueValidationInterceptor implements HandlerInterceptor {

	private final QueueService queueService;
	private final ObjectMapper objectMapper;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		QueueTokenDto tokenDto = parseToken(request, "X-Waiting-Token");

		Optional<QueueTokenResult> activeToken = queueService.getActiveQueueToken(tokenDto);
		if (activeToken.isPresent()) {
			request.setAttribute("queueToken", activeToken.get());
			return true;
		}

		QueueTokenResult waitingToken = queueService.getWaitingQueueToken(tokenDto);
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		response.setContentType("application/json");
		response.getWriter().write(objectMapper.writeValueAsString(QueueTokenResponse.from(waitingToken)));
		return false;
	}

	private QueueTokenDto parseToken(HttpServletRequest request, String tokenName) {
		String jsonWaitingToken = request.getHeader(tokenName);
		if (jsonWaitingToken == null) {
			throw new CustomException(TokenErrorCode.NOT_FOUND_QUEUE);
		}

		try {
			return objectMapper.readValue(jsonWaitingToken, QueueTokenDto.class);
		} catch (Exception e) {
			throw new CustomException(TokenErrorCode.INVALID_QUEUE);
		}
	}

}
