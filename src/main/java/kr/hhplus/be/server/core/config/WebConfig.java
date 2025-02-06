package kr.hhplus.be.server.core.config;

import kr.hhplus.be.server.api.queue.presentation.interceptor.ExpireQueueInterceptor;
import kr.hhplus.be.server.api.queue.presentation.interceptor.QueueValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("!load-test") // TODO : 대기열 없이 부하테스트 진행하기 위해 임시로 비활성화함. 추후 대기열 개선하여 해당 설정 제거 예정
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
