package kr.hhplus.be.server.core.config;

import kr.hhplus.be.server.api.queue.presentation.interceptor.ExpireQueueInterceptor;
import kr.hhplus.be.server.api.queue.presentation.interceptor.QueueValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final QueueValidationInterceptor queueValidationInterceptor;
	private final ExpireQueueInterceptor expireQueueInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(queueValidationInterceptor)
				.addPathPatterns("/concerts/**", "/reservations/payments");

		registry.addInterceptor(expireQueueInterceptor)
				.addPathPatterns("/reservations/payments");

	}
}
