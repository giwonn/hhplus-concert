package kr.hhplus.be.server.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class ApiLoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		long startTime = System.currentTimeMillis();
		try {
			chain.doFilter(request, response);
		} finally {
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;

			String method = request.getMethod();
			String path = request.getRequestURI();
			int status = response.getStatus();

			if (!(path.equals("/actuator/prometheus") && status == 200)) {
				log.info("[{}] {} {} - {} ms", method, path, status, duration);
			}
		}
	}
}
