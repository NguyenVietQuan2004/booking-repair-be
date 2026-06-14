package com.hange.booking.auth.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestRegisterDTO {
	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	private String email;

	@NotBlank(message = "Password không được để trống")
	private String password;
}
