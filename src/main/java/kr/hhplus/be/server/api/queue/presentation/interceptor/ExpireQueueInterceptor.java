package kr.hhplus.be.server.api.queue.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.api.queue.application.QueueService;
import kr.hhplus.be.server.api.queue.application.port.out.QueueTokenResult;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class ExpireQueueInterceptor implements HandlerInterceptor {

	private final QueueService queueService;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
		QueueTokenResult token = (QueueTokenResult) request.getAttribute("queueToken");
		queueService.removeActiveTokenByUserId(token.userId());
	}

}
