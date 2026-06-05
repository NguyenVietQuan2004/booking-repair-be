package com.hange.booking.auth.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hange.booking.auth.dto.user.RequestUpdateUserDTO;
import com.hange.booking.auth.entity.user.User;
import com.hange.booking.auth.service.user.UserService;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseFormat;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseUtil;
import com.hange.booking.auth.utils.mapper.UserMapper;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorAuthCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserMapper userMapper;
	private final UserService userService;

	@GetMapping("/me")
	public ResponseEntity<ApiResponseFormat> getMe(Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AppRuntimeException(ErrorAuthCode.UNAUTHORIZED);
		}

		String email = authentication.getName();

		User user = userService.getUserByEmail(email);

		return ResponseEntity.ok().body(ApiResponseUtil.success(userMapper.toUserDTO(user), HttpStatus.OK.value()));
	}

	@PostMapping("/update-profile")
	public ResponseEntity<ApiResponseFormat> updateProfile(Authentication authentication,
			@RequestBody RequestUpdateUserDTO request) {

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AppRuntimeException(ErrorAuthCode.UNAUTHORIZED);
		}

		String email = authentication.getName();

		User updatedUser = userService.updateUserProfile(email, request);

		return ResponseEntity.ok(ApiResponseUtil.success(userMapper.toUserDTO(updatedUser), HttpStatus.OK.value()));
	}
}