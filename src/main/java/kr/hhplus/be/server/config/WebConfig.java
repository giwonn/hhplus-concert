package kr.hhplus.be.server.config;

import kr.hhplus.be.server.api.token.presentation.interceptor.QueueValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final QueueValidationInterceptor queueValidationInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(queueValidationInterceptor)
				.addPathPatterns("/concerts/**", "/reservations/payments");
	}

}
