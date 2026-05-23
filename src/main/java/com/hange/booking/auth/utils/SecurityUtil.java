package com.hange.booking.auth.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.hange.booking.auth.exception.AppRuntimeException;
import com.hange.booking.auth.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityUtil {

	@Value("${JWT_ACCESS_TOKEN_EXPIRATION}")
	private Long jwt_access_token_expiration;

	@Value("${JWT_REFRESH_TOKEN_EXPIRATION}")
	private Long jwt_refresh_token_expiration;
	public static final MacAlgorithm JWT_Al = MacAlgorithm.HS256;
	private final JwtEncoder jwtEncoder;
	private final JwtDecoder jwtDecoder;

	public Long getRefreshTokenExpiration() {
		return jwt_refresh_token_expiration;
	}

	public String createAccessToken(String subject, String authorities, Integer tokenVersion) {

		Instant now = Instant.now();
		Instant validity = now.plus(jwt_access_token_expiration, ChronoUnit.SECONDS);
		if (authorities == null) {
			throw new AppRuntimeException(ErrorCode.ROLE_NOT_FOUND);

		}
		// @formatter:off
	    JwtClaimsSet claims = JwtClaimsSet.builder()
	        .issuedAt(now)
	        .expiresAt(validity)
//	        .expiresAt(now.plus(15, ChronoUnit.SECONDS))
			// sub xác định cho biết token này thuộc về ai
	        .subject(subject)
			// claim là thông tin thêm 
	        .claim("roles", authorities)
	        .claim("ver", tokenVersion)
	        .build();
	    // @formatter:on

		JwsHeader jwsHeader = JwsHeader.with(JWT_Al).build();
		return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
	}

	public String createRefreshToken(String subject) {
		Instant now = Instant.now();
		Instant validity = now.plus(jwt_refresh_token_expiration, ChronoUnit.SECONDS);

		// @formatter:off
	    JwtClaimsSet claims = JwtClaimsSet.builder()
	        .issuedAt(now)
//	        .expiresAt(now.plus(15, ChronoUnit.SECONDS))
	        .expiresAt(validity)
	        .subject(subject)
	        .build();

		JwsHeader jwsHeader = JwsHeader.with(JWT_Al).build();
		return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
	}

	public Jwt checkValidRefreshToken(String token) {
		try {
			return jwtDecoder.decode(token);
		} catch (Exception e) {
			System.out.println(">> JWT error: " + e.getMessage());
			throw new RuntimeException("Decode token error " + e);
		}
	}

}
