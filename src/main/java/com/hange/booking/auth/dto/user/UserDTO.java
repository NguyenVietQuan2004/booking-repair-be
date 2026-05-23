package com.hange.booking.auth.dto.user;

import java.time.LocalDateTime;

import com.hange.booking.auth.entity.user.AccountStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private Long id;
	private String email;

	private String fullName;
	private String phone;
	private String avatarUrl;
	private String address;

	private Boolean emailVerified;

	private String role;

	private AccountStatusEnum accountStatus;

	private LocalDateTime lastLoginAt;

	private Integer failedLoginCount;
	private LocalDateTime lockedUntil;
	private LocalDateTime nextBookingAllowedAt;
	private LocalDateTime passwordChangedAt;

	private Integer tokenVersion;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;
}