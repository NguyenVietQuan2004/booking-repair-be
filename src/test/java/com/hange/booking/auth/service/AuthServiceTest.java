package com.hange.booking.auth.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hange.booking.auth.dto.user.RequestRegisterDTO;
import com.hange.booking.auth.entity.role.Role;
import com.hange.booking.auth.entity.role.RoleUserEnum;
import com.hange.booking.auth.service.auth.AuthService;
import com.hange.booking.auth.service.role.RoleService;
import com.hange.booking.auth.service.user.TokenService;
import com.hange.booking.auth.service.user.UserService;
import com.hange.booking.auth.service.user.VerificationTokenService;
import com.hange.booking.common.exception.AppRuntimeException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserService userService;

	@Mock
	private TokenService tokenService;

	@Mock
	private RoleService roleService;

	@Mock
	private EmailService emailService;

	@Mock
	private VerificationTokenService verificationTokenService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	@Test
	void shouldRegisterSuccessfully() {

		RequestRegisterDTO request = new RequestRegisterDTO();
		request.setEmail("test@gmail.com");
		request.setPassword("123456");

		Role role = mock(Role.class);

		when(userService.isEmailExisted(request.getEmail())).thenReturn(false);
		when(roleService.getRole(RoleUserEnum.USER.name())).thenReturn(role);
		when(userService.createUser(any(), any(), any()))
				.thenReturn(mock(com.hange.booking.auth.entity.user.User.class));
		when(verificationTokenService.createEmailVerifyToken(any())).thenReturn("token123");

		authService.register(request);

		verify(userService).isEmailExisted(request.getEmail());
		verify(roleService).getRole(RoleUserEnum.USER.name());
		verify(userService).createUser(any(), any(), any());
		verify(verificationTokenService).createEmailVerifyToken(any());
		verify(emailService).sendVerifyEmail(any(), eq("token123"));
	}

	@Test
	void shouldThrowWhenEmailAlreadyExists() {

		RequestRegisterDTO request = new RequestRegisterDTO();
		request.setEmail("test@gmail.com");

		when(userService.isEmailExisted(request.getEmail())).thenReturn(true);

		assertThrows(AppRuntimeException.class, () -> authService.register(request));

		verify(userService).isEmailExisted(request.getEmail());

		verifyNoInteractions(roleService);
		verifyNoInteractions(emailService);
	}
}