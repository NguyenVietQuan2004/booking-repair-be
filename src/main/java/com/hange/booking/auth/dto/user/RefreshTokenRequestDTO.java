package com.hange.booking.auth.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDTO {

	@NotBlank(message = "Refresh token must not be blank")
	private String refreshToken;
}