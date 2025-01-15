package kr.hhplus.be.server.api.token.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.api.token.application.TokenService;
import kr.hhplus.be.server.api.token.application.port.in.QueueTokenDto;
import kr.hhplus.be.server.api.token.application.port.out.QueueTokenResult;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class ExpireQueueInterceptor implements HandlerInterceptor {

	private final TokenService tokenService;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
		QueueTokenResult token = (QueueTokenResult) request.getAttribute("queueToken");
		tokenService.expireQueueToken(new QueueTokenDto(token.id(), token.userId()));
	}

}
