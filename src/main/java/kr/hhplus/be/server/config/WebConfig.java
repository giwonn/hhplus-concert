package kr.hhplus.be.server.config;

import jakarta.servlet.Filter;
import kr.hhplus.be.server.api.token.presentation.interceptor.ExpireQueueInterceptor;
import kr.hhplus.be.server.api.token.presentation.interceptor.QueueValidationInterceptor;
import kr.hhplus.be.server.filter.ApiLoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
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

	@Bean
	public FilterRegistrationBean<Filter> logFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(new ApiLoggingFilter());
		filterRegistrationBean.setOrder(1);
		filterRegistrationBean.addUrlPatterns("/*");

		return filterRegistrationBean;
	}
}
