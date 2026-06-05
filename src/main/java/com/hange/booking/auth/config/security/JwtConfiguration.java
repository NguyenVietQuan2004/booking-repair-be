package com.hange.booking.auth.config.security;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hange.booking.auth.service.user.UserService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JwtConfiguration {

	@Value("${JWT_SECRET}")
	private String jwtKey;
	private static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;
	private final UserService userService;

	private SecretKey getSecretKey() {
		byte[] keyBytes = jwtKey.getBytes();
		return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
	}

	@Bean
	JwtEncoder jwtEncoder() {
		return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
	}

	@Bean
	JwtDecoder jwtDecoder() {
		NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(JWT_ALGORITHM)
				.build();
		return token -> {
			try {
				Jwt jwt = jwtDecoder.decode(token);
				userService.validateTokenVersion(jwt);
				Map<String, Object> safeClaims = new LinkedHashMap<>();

				jwt.getClaims().forEach((k, v) -> {
					if (v instanceof java.time.Instant instant) {
						safeClaims.put(k, instant.toString());
					} else {
						safeClaims.put(k, v);
					}
				});

				Map<String, Object> fullJwt = new LinkedHashMap<>();
				fullJwt.put("claims", safeClaims);
				fullJwt.put("subject", jwt.getSubject());

				ObjectMapper mapper = new ObjectMapper();

				String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fullJwt);

				System.out.println("FULL JWT INFO:\n" + json);

				return jwt;
			} catch (Exception e) {
				System.out.println(">>> JWT error: " + e.getMessage());
				throw new org.springframework.security.authentication.BadCredentialsException("Invalid JWT", e);
			}
		};
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
// Tạo converter cho authorities
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//		đọc và convert thêm tiền tố vì hệ thống so với ROLE_User
		grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
//		“Role của user nằm ở key nào trong JWT?”
		grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
// Tạo converter chính cho JWT - chuyển từ
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

		return jwtAuthenticationConverter;
	}

}
