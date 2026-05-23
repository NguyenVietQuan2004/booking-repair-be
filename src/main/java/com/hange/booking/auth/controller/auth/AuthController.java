package com.hange.booking.auth.controller.auth;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hange.booking.auth.dto.user.LogoutRequestDTO;
import com.hange.booking.auth.dto.user.RefreshTokenRequestDTO;
import com.hange.booking.auth.dto.user.RequestChangePasswordDTO;
import com.hange.booking.auth.dto.user.RequestForgotPasswordDTO;
import com.hange.booking.auth.dto.user.RequestRegisterDTO;
import com.hange.booking.auth.dto.user.RequestResetPasswordDTO;
import com.hange.booking.auth.dto.user.RequestUserLoginDTO;
import com.hange.booking.auth.dto.user.ResendVerificationRequestDTO;
import com.hange.booking.auth.dto.user.ResponseLoginDTO;
import com.hange.booking.auth.entity.user.User;
import com.hange.booking.auth.exception.AppRuntimeException;
import com.hange.booking.auth.exception.ErrorCode;
import com.hange.booking.auth.service.auth.AuthService;
import com.hange.booking.auth.service.user.TokenService;
import com.hange.booking.auth.service.user.UserService;
import com.hange.booking.auth.utils.SecurityUtil;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseFormat;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseUtil;
import com.hange.booking.auth.utils.mapper.UserMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserMapper userMapper;
	private final AuthService authService;
	private final UserService userService;
	private final TokenService tokenService;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final SecurityUtil securityUtil;

	@PostMapping("/register")
	public ResponseEntity<ApiResponseFormat> Register(@Valid @RequestBody RequestRegisterDTO requestRegisterData) {
		authService.register(requestRegisterData);
		return ResponseEntity.ok().body(ApiResponseUtil.success("Register token email sent", HttpStatus.OK.value()));
	}

	@GetMapping("/verify-email")
	public ResponseEntity<ApiResponseFormat> verifyEmail(@RequestParam(name = "token") String token) {

		authService.verifyEmail(token);
		return ResponseEntity.ok().body(ApiResponseUtil.success("Verify email success", HttpStatus.OK.value()));
	}

	@PostMapping("/resend-verification")
	public ResponseEntity<ApiResponseFormat> resendVerification(
			@RequestBody @Valid ResendVerificationRequestDTO resendVerificationRequest) {
		authService.resendVerificationEmail(resendVerificationRequest);
		return ResponseEntity.ok(ApiResponseUtil.success("Verification email resent", HttpStatus.OK.value()));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponseFormat> login(@RequestBody @Valid RequestUserLoginDTO requestUserLoginData,
			HttpServletRequest request) {

		String email = requestUserLoginData.getEmail();
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
				requestUserLoginData.getPassword());

		try {

			Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			User user = userService.getUserByEmail(email);

			userService.updateLoginSuccess(user);
			String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.collect(Collectors.joining(" "));

			String accessToken = securityUtil.createAccessToken(email, authorities, user.getTokenVersion());
			String refreshToken = securityUtil.createRefreshToken(email);

			tokenService.saveRefreshToken(user, refreshToken, request);

			ResponseLoginDTO response = new ResponseLoginDTO();
			response.setAccessToken(accessToken);
			response.setRefreshToken(refreshToken);

			response.setUserDTO(userMapper.toUserDTO(user));
			return ResponseEntity.ok().body(ApiResponseUtil.success(response, HttpStatus.OK.value()));

		} catch (BadCredentialsException ex) {

			userService.increaseFailedLogin(email);
			throw new AppRuntimeException(ErrorCode.AUTH_INVALID_CREDENTIALS);
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponseFormat> logout(@RequestBody @Valid LogoutRequestDTO logoutRequestDTO) {

		tokenService.logout(logoutRequestDTO);

		return ResponseEntity.ok()
				.body(ApiResponseUtil.success(logoutRequestDTO.getRefreshToken(), HttpStatus.OK.value()));
	}

	@PostMapping("/change-password")
	public ResponseEntity<ApiResponseFormat> changePassword(@RequestBody @Valid RequestChangePasswordDTO request,
			Authentication authentication) {

		String email = authentication.getName();

		userService.changePassword(email, request);
		return ResponseEntity.ok().body(ApiResponseUtil.success("Change password success", HttpStatus.OK.value()));
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<ApiResponseFormat> refreshToken(@RequestBody @Valid RefreshTokenRequestDTO requestDTO,
			HttpServletRequest request) {
		// 1. verify + lấy user
		User user = tokenService.verifyAndGetUser(requestDTO);

		// 2. lấy authority từ user
		String authorities = user.getRole().getName();

		// 3. tạo access token mới
		String newAccessToken = securityUtil.createAccessToken(user.getEmail(), authorities, user.getTokenVersion());

		// 4. rotate refresh token (🔥 rất quan trọng)
		String newRefreshToken = securityUtil.createRefreshToken(user.getEmail());

		tokenService.rotateRefreshToken(user, requestDTO.getRefreshToken(), newRefreshToken, request);

		// 5. response
		ResponseLoginDTO response = new ResponseLoginDTO();
		response.setAccessToken(newAccessToken);
		response.setRefreshToken(newRefreshToken);
		response.setUserDTO(userMapper.toUserDTO(user));

		return ResponseEntity.ok().body(ApiResponseUtil.success(response, HttpStatus.OK.value()));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponseFormat> forgotPassword(@RequestBody @Valid RequestForgotPasswordDTO request) {

		authService.forgotPassword(request.getEmail());

		return ResponseEntity.ok().body(ApiResponseUtil.success("Reset password email sent", HttpStatus.OK.value()));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponseFormat> resetPassword(@RequestBody @Valid RequestResetPasswordDTO request) {

		authService.resetPassword(request);

		return ResponseEntity.ok().body(ApiResponseUtil.success("Reset password success", HttpStatus.OK.value()));
	}

}
