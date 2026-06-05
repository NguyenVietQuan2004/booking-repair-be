package com.hange.booking.auth.service.user;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.hange.booking.auth.dto.user.RequestChangePasswordDTO;
import com.hange.booking.auth.dto.user.RequestUpdateUserDTO;
import com.hange.booking.auth.entity.role.Role;
import com.hange.booking.auth.entity.user.AccountStatusEnum;
import com.hange.booking.auth.entity.user.PasswordChangeOption;
import com.hange.booking.auth.entity.user.User;
import com.hange.booking.auth.repository.UserRepository;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorAuthCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	@Value("${LOCK_TIME}")
	private Long lockTimeSeconds;
	@Value("${MAX_FAILED_LOGIN}")
	private Integer maxFailedLogin;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new AppRuntimeException(ErrorAuthCode.USER_NOT_FOUND));
	}

	public User getUserByEmail(String email) {

		return userRepository.findByEmail(email).orElseThrow(() -> new AppRuntimeException(ErrorAuthCode.USER_NOT_FOUND));
	}

	public void updateLoginSuccess(User user) {
		user.setFailedLoginCount(0);
		user.setLastLoginAt(LocalDateTime.now());
		userRepository.save(user);
	}

	public void increaseFailedLogin(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new AppRuntimeException(ErrorAuthCode.USER_NOT_FOUND));

		int count = user.getFailedLoginCount() == null ? 0 : user.getFailedLoginCount();
		user.setFailedLoginCount(count + 1);

		if (user.getFailedLoginCount() >= maxFailedLogin) {
			user.setLockedUntil(LocalDateTime.now().plusSeconds(lockTimeSeconds));
		}
		userRepository.save(user);
	}

	public void changePassword(String email, RequestChangePasswordDTO request) {
		User user = this.getUserByEmail(email);

		if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
			throw new AppRuntimeException(ErrorAuthCode.USER_LOCKED);
		}

		if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
			throw new AppRuntimeException(ErrorAuthCode.AUTH_INVALID_CREDENTIALS);
		}

		if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
			throw new AppRuntimeException(ErrorAuthCode.PASSWORD_CONFIRMATION_MISMATCH);
		}
		this.validatePasswordPolicy(request.getNewPassword());
		if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
			throw new AppRuntimeException(ErrorAuthCode.PASSWORD_CONFIRMATION_MISMATCH);
		}

		updatePasswordAndLastChangePass(user, request.getNewPassword());
		PasswordChangeOption option = request.getOption() == null ? PasswordChangeOption.KEEP_ALL : request.getOption();
		tokenService.handleSessionAfterPasswordChange(user, option, request.getRefreshToken());
	}

	public void validateTokenVersion(Jwt jwt) {
		String email = jwt.getSubject();
		Number ver = jwt.getClaim("ver");
		int tokenVersionInToken = ver.intValue();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new AppRuntimeException(ErrorAuthCode.USER_NOT_FOUND));

		if (!user.getTokenVersion().equals(tokenVersionInToken)) {
			throw new AppRuntimeException(ErrorAuthCode.INVALID_TOKEN_VERSION);
		}
	}

	public void updatePasswordAndLastChangePass(User user, String newPassword) {
		user.setPasswordHash(passwordEncoder.encode(newPassword));
		user.setTokenVersion(user.getTokenVersion() == null ? 1 : user.getTokenVersion() + 1);
		user.setPasswordChangedAt(LocalDateTime.now());
		userRepository.save(user);
	}

	public Boolean isEmailExisted(String email) {
		return userRepository.existsByEmail(email);
	}

	public User createUser(String email, String password, Role role) {
		System.out.println(password);
		this.validatePasswordPolicy(password);
		User user = new User();
		user.setEmail(email);
		user.setPasswordHash(passwordEncoder.encode(password));
		user.setRole(role);
		return userRepository.save(user);
	}

	public void activateFromVerificationToken(User user) {
		user.setEmailVerified(true);
		user.setAccountStatus(AccountStatusEnum.ACTIVE);
		userRepository.save(user);
	}

	public User updateUserProfile(String email, RequestUpdateUserDTO request) {

		User user = getUserByEmail(email);

		if (request.getFullName() != null) {
			user.setFullName(request.getFullName());
		}

		if (request.getPhone() != null) {
			user.setPhone(request.getPhone());
		}

		if (request.getAvatarUrl() != null) {
			user.setAvatarUrl(request.getAvatarUrl());
		}

		if (request.getAddress() != null) {
			user.setAddress(request.getAddress());
		}

		return userRepository.save(user);
	}

	public void validateUser(User user) {

		if (user.getAccountStatus() != AccountStatusEnum.ACTIVE) {
			throw new AppRuntimeException(ErrorAuthCode.USER_NOT_ACTIVE);
		}

		if (!Boolean.TRUE.equals(user.getEmailVerified())) {
			throw new AppRuntimeException(ErrorAuthCode.EMAIL_NOT_VERIFIED);
		}

		if (user.getRole() == null) {
			throw new AppRuntimeException(ErrorAuthCode.ROLE_NOT_FOUND);
		}

		if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
			throw new AppRuntimeException(ErrorAuthCode.USER_LOCKED);
		}
	}

	public void validatePasswordPolicy(String password) {
		String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$";
		if (password == null || password.isBlank()) {
			throw new AppRuntimeException(ErrorAuthCode.PASSWORD_REQUIRED);
		}

		if (!password.matches(PASSWORD_PATTERN)) {
			throw new AppRuntimeException(ErrorAuthCode.PASSWORD_POLICY_VIOLATION);
		}
	}

}