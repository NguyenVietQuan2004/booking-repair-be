package com.hange.booking.auth.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestResetPasswordDTO {

	@NotBlank(message = "RESET_PASSWORD_TOKEN_NOT_BLANK")
	private String token;

	@NotBlank(message = "NEW_PASSWORD_NOT_BLANK")
	private String newPassword;
	@NotBlank(message = "CONFIRM_PASSWORD_NOT_BLANK")
	private String confirmPassword;
}