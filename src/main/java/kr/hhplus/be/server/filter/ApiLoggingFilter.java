package kr.hhplus.be.server.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ApiLoggingFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) {
		log.info("{} init", this.getClass().getSimpleName());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		long startTime = System.currentTimeMillis();
		try {
			chain.doFilter(request, response);
		} finally {
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;

			String method = httpRequest.getMethod();
			String path = httpRequest.getRequestURI();
			int status = httpResponse.getStatus();

			log.info("[{}] {} {} - {} ms", method, path, status, duration);
		}
	}

	@Override
	public void destroy() {
		log.info("{} destroy", this.getClass().getSimpleName());
	}
}
