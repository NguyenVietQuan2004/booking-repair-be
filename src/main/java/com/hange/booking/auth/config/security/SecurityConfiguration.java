package com.hange.booking.auth.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	private static final String[] SWAGGER_WHITELIST = { "/v3/api-doc/**", "/v3/api-docs/**", "/swagger-ui/**",
			"/swagger-ui.html" };

	private static final String[] STATIC_WHITELIST = { "/storage/**", "/ws/**" };

	// AUTH PUBLIC
	private static final String[] AUTH_PUBLIC = { "/api/v1/auth/login", "/api/v1/auth/register",
			"/api/v1/auth/verify-email", "/api/v1/auth/resend-verification", "/api/v1/auth/forgot-password",
			"/api/v1/auth/reset-password", "/api/v1/auth/refresh-token" };

	// PUBLIC CATALOG (guest can view)
	private static final String[] PUBLIC_GET_API = { "/api/v1/services/**", "/api/v1/categories/**",
			"/api/v1/locations/**", "/api/v1/service-slots/**", "/api/v1/files/**" };

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, AuthenticationEntryPointJwt authEntryPointJwt,
			CustomAccessDeniedHandler customAccessDeniedHandler) throws Exception {
		http.cors(cors -> {
		}).csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize -> authorize
//						.requestMatchers("/v3/api-doc/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
//								"/storage/**")
//						.permitAll()
//						.requestMatchers("/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/verify-email",
//								"/api/v1/auth/resend-verification", "/api/v1/auth/forgot-password",
//								"/api/v1/auth/reset-password", "/api/v1/auth/refresh-token", "/**", "/ws/**")
//						.permitAll()
//						.anyRequest().authenticated()
//							.requestMatchers("/api/v1/**", "/api/v1/files", "/").permitAll()
//						permitall không có nghĩa là ko check - miễn có header là check
//						nó là: bạn có được phép vào không, nhưng vẫn check bạn là ai

						// PUBLIC GROUPS
						.requestMatchers(SWAGGER_WHITELIST).permitAll().requestMatchers(STATIC_WHITELIST).permitAll()
						.requestMatchers(AUTH_PUBLIC).permitAll().requestMatchers(PUBLIC_GET_API).permitAll()

						// PROTECT EVERYTHING ELSE
						.anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
						.authenticationEntryPoint(authEntryPointJwt).accessDeniedHandler(customAccessDeniedHandler));

		return http.build();
	}

}
