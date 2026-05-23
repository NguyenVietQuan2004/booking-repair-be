package com.hange.booking.auth.service.user;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hange.booking.auth.dto.user.LogoutRequestDTO;
import com.hange.booking.auth.dto.user.RefreshTokenRequestDTO;
import com.hange.booking.auth.entity.user.PasswordChangeOption;
import com.hange.booking.auth.entity.user.RefreshToken;
import com.hange.booking.auth.entity.user.User;
import com.hange.booking.auth.exception.AppRuntimeException;
import com.hange.booking.auth.exception.ErrorCode;
import com.hange.booking.auth.repository.RefreshTokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
	private final RefreshTokenRepository refreshTokenRepository;

	@Value("${JWT_REFRESH_TOKEN_EXPIRATION}")
	private Long refreshExpirationDays;

	private String parseDeviceName(String userAgent) {
		if (userAgent == null)
			return "Unknown";

		userAgent = userAgent.toLowerCase();

		if (userAgent.contains("chrome"))
			return "Chrome";
		if (userAgent.contains("firefox"))
			return "Firefox";
		if (userAgent.contains("safari"))
			return "Safari";
		if (userAgent.contains("edge"))
			return "Edge";
		if (userAgent.contains("android"))
			return "Android";
		if (userAgent.contains("iphone"))
			return "iPhone";

		return "Unknown";
	}

	@Transactional
	public void saveRefreshToken(User user, String refreshToken, HttpServletRequest request) {

		LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshExpirationDays);

		// =========================
		// DEVICE METADATA
		// =========================
		String userAgent = request.getHeader("User-Agent");
		String ipAddress = request.getRemoteAddr();

		String deviceName = parseDeviceName(userAgent);

		String deviceId = request.getHeader("X-Device-Id");

		// 3. create token
		RefreshToken token = RefreshToken.builder().tokenHash(refreshToken).user(user).isRevoked(false)
				.expiresAt(expiresAt).deviceId(deviceId).deviceName(deviceName).ipAddress(ipAddress).build();
		// 4. save DB
		refreshTokenRepository.save(token);
	}

	public void logout(LogoutRequestDTO logoutRequestDTO) {

		RefreshToken token = refreshTokenRepository.findByTokenHash(logoutRequestDTO.getRefreshToken())
				.orElseThrow(() -> new AppRuntimeException(ErrorCode.TOKEN_NOT_FOUND));
		if (Boolean.TRUE.equals(token.getIsRevoked())) {
			throw new AppRuntimeException(ErrorCode.TOKEN_ALREADY_REVOKED);
		}
		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new AppRuntimeException(ErrorCode.REFRESH_TOKEN_EXPIRED);
		}
		token.setIsRevoked(true);
		refreshTokenRepository.save(token);
	}

	public void handleSessionAfterPasswordChange(User user, PasswordChangeOption option, String currentRefreshToken) {

		if (option == null) {
			option = PasswordChangeOption.KEEP_ALL;
		}

		switch (option) {

		case REVOKE_ALL -> {
			refreshTokenRepository.revokeAllByUserId(user.getId());
		}

		case KEEP_CURRENT -> {
			// 1. tìm token hiện tại
			RefreshToken current = refreshTokenRepository.findByTokenHash(currentRefreshToken)
					.orElseThrow(() -> new RuntimeException("Token not found"));

			// 2. check ownership
			if (!current.getUser().getId().equals(user.getId())) {
				throw new AppRuntimeException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
				// không nên báo rõ để tránh leak info
			}

			// 3. check revoked
			if (current.getIsRevoked()) {
				throw new AppRuntimeException(ErrorCode.REFRESH_TOKEN_REVOKED);
			}

			// 4. check expired
			if (current.getExpiresAt().isBefore(LocalDateTime.now())) {
				throw new AppRuntimeException(ErrorCode.REFRESH_TOKEN_REVOKED);
			}

			// 5. revoke tất cả trừ token hiện tại
			refreshTokenRepository.revokeAllExcept(user.getId(), current.getId());
		}

		case KEEP_ALL -> {
			// ❌ không làm gì (không khuyến khích)
		}
		}
	}

	public User verifyAndGetUser(RefreshTokenRequestDTO refreshTokenRequestDTO) {

		String tokenHash = refreshTokenRequestDTO.getRefreshToken();
		RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
				.orElseThrow(() -> new AppRuntimeException(ErrorCode.INVALID_REFRESH_TOKEN));
		if (Boolean.TRUE.equals(token.getIsRevoked())) {
			throw new AppRuntimeException(ErrorCode.REFRESH_TOKEN_REVOKED);
		}

		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new AppRuntimeException(ErrorCode.REFRESH_TOKEN_EXPIRED);
		}
		User user = token.getUser();
		if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
			throw new AppRuntimeException(ErrorCode.USER_LOCKED);
		}
		return user;
	}

	public void rotateRefreshToken(User user, String oldToken, String newToken, HttpServletRequest request) {
		String oldHash = (oldToken);

		RefreshToken token = refreshTokenRepository.findByTokenHash(oldHash)
				.orElseThrow(() -> new AppRuntimeException(ErrorCode.INVALID_REFRESH_TOKEN));

		// revoke token cũ
		token.setIsRevoked(true);
		refreshTokenRepository.save(token);

		// lưu token mới
		this.saveRefreshToken(user, newToken, request);
	}

}
