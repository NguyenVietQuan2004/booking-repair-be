package com.hange.booking.auth.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendVerificationRequestDTO {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	// getter setter
}