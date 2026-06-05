package com.hange.booking.auth.config.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
	private final PermissionInterceptor permissionInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String[] whiteList = { "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/storage/**",

				"/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/verify-email",
				"/api/v1/auth/resend-verification", "/api/v1/auth/forgot-password", "/api/v1/auth/reset-password",
				"/api/v1/auth/refresh-token", "/" };

		registry.addInterceptor(permissionInterceptor).excludePathPatterns(whiteList);
	}
}
