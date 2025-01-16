package kr.hhplus.be.server.api.token.presentation.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.api.token.application.port.in.QueueTokenDto;
import kr.hhplus.be.server.api.token.exception.TokenErrorCode;
import kr.hhplus.be.server.api.token.application.TokenService;
import kr.hhplus.be.server.api.token.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.api.token.presentation.port.out.QueueTokenResponse;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class QueueValidationInterceptor implements HandlerInterceptor {

	private final TokenService tokenService;
	private final ObjectMapper objectMapper;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		QueueTokenDto tokenDto = parseToken(request, "X-Waiting-Token");
		QueueTokenResult queueToken = tokenService.checkQueuePassedAndUpdateToken(tokenDto);

		// 202 응답으로 토큰 반환
		if (queueToken.waitingNumber() != 0) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			response.setContentType("application/json");
			response.getWriter().write(objectMapper.writeValueAsString(QueueTokenResponse.from(queueToken)));
			return false;
		}

		request.setAttribute("queueToken", queueToken);
		// 대기열 통과한 토큰이면 요청을 이어서 수행함
		return true;
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
