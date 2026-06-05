package com.hange.booking.auth.service.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hange.booking.auth.entity.user.TokenType;
import com.hange.booking.auth.entity.user.User;
import com.hange.booking.auth.entity.user.VerificationToken;
import com.hange.booking.auth.repository.VerificationTokenRepository;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorAuthCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

	private final VerificationTokenRepository verificationTokenRepository;
	@Value("${VERIFICATION_TOKEN_EXPIRATION}")
	private Long VERIFICATION_TOKEN_EXPIRATION;

	public String createEmailVerifyToken(User user) {
		String rawToken = createVerificationToken(user, TokenType.EMAIL_VERIFY);
		return rawToken;
	}

	public String createResetPasswordToken(User user) {
		String rawToken = createVerificationToken(user, TokenType.RESET_PASSWORD);
		return rawToken;
	}

	public VerificationToken verify(String rawToken, TokenType type) {
		String hashToken = rawToken;
		VerificationToken token = verificationTokenRepository.findByTokenHashAndType(hashToken, type)
				.orElseThrow(() -> new AppRuntimeException(ErrorAuthCode.TOKEN_TYPE_INVALID));

		validate(token);
		return token;
	}

	private String createVerificationToken(User user, TokenType type) {
		String rawToken = generateToken();
		String tokenHash = rawToken;
		VerificationToken token = VerificationToken.builder().tokenHash(tokenHash).user(user).type(type).used(false)
				.expiresAt(LocalDateTime.now().plusSeconds(VERIFICATION_TOKEN_EXPIRATION)).build();
		verificationTokenRepository.save(token);
		return rawToken;
	}

	private void validate(VerificationToken token) {
		if (Boolean.TRUE.equals(token.getUsed()))
			throw new AppRuntimeException(ErrorAuthCode.TOKEN_ALREADY_USED);

		if (token.getExpiresAt().isBefore(LocalDateTime.now()))
			throw new AppRuntimeException(ErrorAuthCode.TOKEN_EXPIRED);
	}

	public void markAsUsed(VerificationToken token) {

		token.setUsed(true);

		verificationTokenRepository.save(token);
	}

	public String generateToken() {
		return UUID.randomUUID().toString();
	}
}