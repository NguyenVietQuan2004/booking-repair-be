package com.hange.booking.auth.service.auth;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hange.booking.auth.dto.user.RequestRegisterDTO;
import com.hange.booking.auth.dto.user.RequestResetPasswordDTO;
import com.hange.booking.auth.dto.user.ResendVerificationRequestDTO;
import com.hange.booking.auth.entity.role.Role;
import com.hange.booking.auth.entity.role.RoleUserEnum;
import com.hange.booking.auth.entity.user.PasswordChangeOption;
import com.hange.booking.auth.entity.user.TokenType;
import com.hange.booking.auth.entity.user.User;
import com.hange.booking.auth.entity.user.VerificationToken;
import com.hange.booking.auth.service.EmailService;
import com.hange.booking.auth.service.role.RoleService;
import com.hange.booking.auth.service.user.TokenService;
import com.hange.booking.auth.service.user.UserService;
import com.hange.booking.auth.service.user.VerificationTokenService;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorAuthCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserService userService;
	private final TokenService tokenService;
	private final RoleService roleService;
	private final EmailService emailService;
	private final VerificationTokenService verificationTokenService;
	private final PasswordEncoder passwordEncoder;

	public void register(RequestRegisterDTO requestRegisterData) {
		if (userService.isEmailExisted(requestRegisterData.getEmail())) {
			throw new AppRuntimeException(ErrorAuthCode.EMAIL_ALREADY_EXISTS);
		}

		Role role = roleService.getRole(RoleUserEnum.USER.name());
		User user = userService.createUser(requestRegisterData.getEmail(), requestRegisterData.getPassword(), role);

		// Create token and send user to verify
		String rawToken = verificationTokenService.createEmailVerifyToken(user);
		emailService.sendVerifyEmail(user.getEmail(), rawToken);
	}

	@Transactional
	public void verifyEmail(String rawToken) {
		VerificationToken token = verificationTokenService.verify(rawToken, TokenType.EMAIL_VERIFY);

		userService.activateFromVerificationToken(token.getUser());
		verificationTokenService.markAsUsed(token);
	}

	public void resendVerificationEmail(ResendVerificationRequestDTO resendVerificationRequest) {

		User user = userService.getUserByEmail(resendVerificationRequest.getEmail());

		if (Boolean.TRUE.equals(user.getEmailVerified())) {
			throw new AppRuntimeException(ErrorAuthCode.EMAIL_ALREADY_VERIFIED);
		}

		if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
			throw new AppRuntimeException(ErrorAuthCode.USER_LOCKED);
		}

		String rawToken = verificationTokenService.createEmailVerifyToken(user);

		emailService.sendVerifyEmail(user.getEmail(), rawToken);
	}

	public void forgotPassword(String email) {

		User user = userService.getUserByEmail(email);

		if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
			throw new AppRuntimeException(ErrorAuthCode.USER_LOCKED);
		}

		String rawToken = verificationTokenService.createResetPasswordToken(user);
		emailService.sendForgotPasswordEmail(user.getEmail(), rawToken);
	}

	@Transactional
	public void verifyTokenPassword(String rawToken) {
		VerificationToken token = verificationTokenService.verify(rawToken, TokenType.EMAIL_VERIFY);

		verificationTokenService.markAsUsed(token);
	}

	public void resetPassword(RequestResetPasswordDTO request) {
		String rawToken = request.getToken();

		VerificationToken token = verificationTokenService.verify(rawToken, TokenType.RESET_PASSWORD);

		User user = token.getUser();

		if (user == null) {
			throw new AppRuntimeException(ErrorAuthCode.USER_NOT_FOUND);
		}

		if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
			throw new AppRuntimeException(ErrorAuthCode.USER_LOCKED);
		}

		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			throw new AppRuntimeException(ErrorAuthCode.PASSWORD_CONFIRMATION_MISMATCH);
		}
		userService.validatePasswordPolicy(request.getNewPassword());
		if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
			throw new AppRuntimeException(ErrorAuthCode.PASSWORD_SAME_AS_OLD);
		}
		userService.updatePasswordAndLastChangePass(user, request.getNewPassword());
		verificationTokenService.markAsUsed(token);
		tokenService.handleSessionAfterPasswordChange(user, PasswordChangeOption.REVOKE_ALL, null);
	}

}
