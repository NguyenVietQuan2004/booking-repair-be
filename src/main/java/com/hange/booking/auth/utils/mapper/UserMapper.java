package com.hange.booking.auth.utils.mapper;

import org.springframework.stereotype.Component;

import com.hange.booking.auth.dto.user.UserDTO;
import com.hange.booking.auth.entity.user.User;

@Component
public class UserMapper {

	public UserDTO toUserDTO(User user) {
		if (user == null) {
			return null;
		}

		return UserDTO.builder().id(user.getId()).email(user.getEmail()).fullName(user.getFullName())
				.phone(user.getPhone()).avatarUrl(user.getAvatarUrl()).address(user.getAddress())
				.emailVerified(user.getEmailVerified()).role(user.getRole() != null ? user.getRole().getName() : null)
				.accountStatus(user.getAccountStatus()).lastLoginAt(user.getLastLoginAt())
				.failedLoginCount(user.getFailedLoginCount()).lockedUntil(user.getLockedUntil())
				.nextBookingAllowedAt(user.getNextBookingAllowedAt()).passwordChangedAt(user.getPasswordChangedAt())
				.tokenVersion(user.getTokenVersion()).createdAt(user.getCreatedAt()).updatedAt(user.getUpdatedAt())
				.deletedAt(user.getDeletedAt()).build();
	}
}