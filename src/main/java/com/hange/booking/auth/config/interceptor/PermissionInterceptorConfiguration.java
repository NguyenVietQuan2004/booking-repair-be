//package com.hange.booking.auth.config.interceptor;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import lombok.RequiredArgsConstructor;
//
//@Configuration
//@RequiredArgsConstructor
//public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
//	private final PermissionInterceptor permissionInterceptor;
//
//// chỉ dùng cho http request
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		String[] whiteList = { "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/storage/**",
//
//				"/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/verify-email",
//				"/api/v1/auth/resend-verification", "/api/v1/auth/forgot-password", "/api/v1/auth/reset-password",
//				"/api/v1/auth/refresh-token", "/**" };
//
//		registry.addInterceptor(permissionInterceptor).excludePathPatterns(whiteList);
//	}
//}

package com.hange.booking.auth.config.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

	private final PermissionInterceptor permissionInterceptor;

	// =========================
	// SWAGGER / STATIC PUBLIC
	// =========================
	private static final String[] WHITE_LIST = { "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
			"/storage/**" };

	// =========================
	// AUTH PUBLIC ROUTES
	// =========================
	private static final String[] AUTH_WHITE_LIST = { "/api/v1/auth/login", "/api/v1/auth/register",
			"/api/v1/auth/verify-email", "/api/v1/auth/resend-verification", "/api/v1/auth/forgot-password",
			"/api/v1/auth/reset-password", "/api/v1/auth/refresh-token" };

	// =========================
	// PUBLIC GET API (guest can view)
	// =========================
	private static final String[] PUBLIC_GET_API = { "/api/v1/services/**", "/api/v1/categories/**",
			"/api/v1/locations/**", "/api/v1/service-slots/**", "/api/v1/files/**" };

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(permissionInterceptor).excludePathPatterns(WHITE_LIST)
				.excludePathPatterns(AUTH_WHITE_LIST).excludePathPatterns(PUBLIC_GET_API);
	}
}