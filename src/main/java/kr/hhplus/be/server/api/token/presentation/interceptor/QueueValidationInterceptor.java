package kr.hhplus.be.server.api.token.presentation.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.api.token.application.TokenErrorCode;
import kr.hhplus.be.server.api.token.application.TokenService;
import kr.hhplus.be.server.api.token.application.port.in.ValidateQueueTokenDto;
import kr.hhplus.be.server.api.token.application.port.out.QueueTokenResult;
import kr.hhplus.be.server.api.token.presentation.port.out.QueueTokenResponse;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class QueueValidationInterceptor implements HandlerInterceptor {

	private final TokenService tokenService;
	private final ObjectMapper objectMapper;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		String waitingTokenJson = getTokenFromHeader(request, "X-Waiting-Token");
		ValidateQueueTokenDto tokenDto = parseToken(waitingTokenJson, ValidateQueueTokenDto.class);
		QueueTokenResult queueToken = tokenService.checkQueuePassedAndUpdateToken(tokenDto);

		// 202 응답으로 토큰 반환
		if (queueToken.waitingNumber() != 0) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			response.setContentType("application/json");
			response.getWriter().write(objectMapper.writeValueAsString(QueueTokenResponse.from(queueToken)));
			return false;
		}

		// 대기열 통과한 토큰이면 요청을 이어서 수행함
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
		if (!request.getRequestURI().equals("/reservation/payments")) return;

		String waitingTokenJson = getTokenFromHeader(request, "X-Waiting-Token");
		ValidateQueueTokenDto tokenDto = parseToken(waitingTokenJson, ValidateQueueTokenDto.class);
		tokenService.deActivateQueueToken(tokenDto.tokenId());
	}

	private String getTokenFromHeader(HttpServletRequest request, String tokenName) {
		String waitingTokenJson = request.getHeader(tokenName);
		if (waitingTokenJson == null || waitingTokenJson.isEmpty()) {
			throw new CustomException(TokenErrorCode.NOT_FOUND_QUEUE);
		}
		return waitingTokenJson;
	}

	private <T> T parseToken(String token, Class<T> clazz) {
		try {
			return objectMapper.readValue(token, clazz);
		} catch (Exception e) {
			throw new CustomException(TokenErrorCode.INVALID_QUEUE);
		}
	}

}
