package com.hange.booking.auth.dto.user;

import com.hange.booking.auth.entity.user.PasswordChangeOption;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestChangePasswordDTO {

	@NotBlank(message = "Mật khẩu cũ không được để trống")
	private String oldPassword;
	@NotBlank(message = "Mật khẩu mới không được để trống")
	private String newPassword;
	@NotBlank(message = "Xác nhận mật khẩu không được để trống")
	private String confirmNewPassword;

	private PasswordChangeOption option;
	private String refreshToken;
}