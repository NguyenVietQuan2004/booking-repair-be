
package com.hange.booking.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorAuthCode implements ErrorCode {

	/*
	 * ================================================= USER
	 * =================================================
	 */
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"), USER_NOT_ACTIVE(HttpStatus.FORBIDDEN, "User is not active"),
	USER_LOCKED(HttpStatus.LOCKED, "User is locked due to multiple failed attempts"),
	USER_DISABLED(HttpStatus.FORBIDDEN, "Account is disabled"),

	/*
	 * ================================================= EMAIL
	 * =================================================
	 */
	EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Email already exists"),
	EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "Email is not verified"),
	EMAIL_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "Email already verified"),

	/*
	 * ================================================= AUTH
	 * =================================================
	 */
	AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),
	TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "Too many requests"),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),

	/*
	 * ================================================= ROLE (GENERAL)
	 * =================================================
	 */
	ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Role not found"),
	ROLE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Role already exists"),
	INVALID_ROLE(HttpStatus.BAD_REQUEST, "Invalid role"),
	INVALID_ROLE_NAME(HttpStatus.BAD_REQUEST, "Invalid role name"),
	ROLE_IN_USE(HttpStatus.BAD_REQUEST, "Role is currently assigned to users"),
	ROLE_CANNOT_BE_DELETED(HttpStatus.BAD_REQUEST, "This role cannot be deleted"),
	ROLE_ALREADY_ASSIGNED(HttpStatus.BAD_REQUEST, "Role already assigned"),
	ROLE_NOT_ASSIGNED(HttpStatus.BAD_REQUEST, "Role not assigned"),

	/*
	 * ================================================= PERMISSION
	 * =================================================
	 */
	PERMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Permission not found"),
	PERMISSION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Permission already exists"),
	PERMISSION_ALREADY_ASSIGNED(HttpStatus.BAD_REQUEST, "Permission already assigned"),
	PERMISSION_NOT_ASSIGNED(HttpStatus.BAD_REQUEST, "Permission not assigned"),
	INVALID_PERMISSION(HttpStatus.BAD_REQUEST, "Invalid permission"),

	/*
	 * ================================================= ROLE - PERMISSION RELATION
	 * =================================================
	 */
	ROLE_PERMISSION_CONFLICT(HttpStatus.BAD_REQUEST, "Role permission conflict"),
	ROLE_HAS_NO_PERMISSIONS(HttpStatus.BAD_REQUEST, "Role has no permissions"),

	/*
	 * ================================================= TOKEN (GENERAL)
	 * =================================================
	 */
	TOKEN_INVALID(HttpStatus.BAD_REQUEST, "Invalid token"), TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Token expired"),
	TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "Token revoked"), TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Token not found"),
	TOKEN_ALREADY_REVOKED(HttpStatus.CONFLICT, "Token already revoked"),
	TOKEN_ALREADY_USED(HttpStatus.BAD_REQUEST, "Token already used"),
	TOKEN_TYPE_INVALID(HttpStatus.BAD_REQUEST, "Invalid token type"),
	INVALID_TOKEN_VERSION(HttpStatus.BAD_REQUEST, "Invalid token version"),

	/*
	 * ================================================= REFRESH TOKEN
	 * =================================================
	 */
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid refresh token"),
	REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh token expired"),
	REFRESH_TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "Refresh token revoked"),
	REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Refresh token not found"),

	/*
	 * ================================================= PASSWORD
	 * =================================================
	 */
	PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "Password is required"),
	PASSWORD_POLICY_VIOLATION(HttpStatus.BAD_REQUEST,
			"Password must be at least 8 characters, include uppercase, lowercase, number and special character"),
	PASSWORD_CONFIRMATION_MISMATCH(HttpStatus.BAD_REQUEST, "Password confirmation does not match"),
	PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "New password must be different from old password"),

	/*
	 * ================================================= VALIDATION
	 * =================================================
	 */
	VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Invalid input format"),
	VALIDATION_MISSING_REQUIRED_PARAM_FIELD(HttpStatus.BAD_REQUEST, "Missing required field");

//56
	private final HttpStatus status;
	private final String message;

	@Override
	public String getCode() {
		return this.name(); // Trả về tên enum (ví dụ: USER_NOT_FOUND)
	}
}