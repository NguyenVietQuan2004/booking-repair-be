package com.hange.booking.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorFileCode implements ErrorCode {

	/* ==================== FILE ==================== */
	FILE_EMPTY(HttpStatus.BAD_REQUEST, "File is empty"), INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "Invalid file name"),
	UNSUPPORTED_EXTENSION(HttpStatus.BAD_REQUEST, "Unsupported file extension"),
	UNSUPPORTED_MIME_TYPE(HttpStatus.BAD_REQUEST, "Unsupported file type"),
	FILE_SIZE_EXCEEDED(HttpStatus.CONTENT_TOO_LARGE, "File size exceeds limit"),
	FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed"),
	FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "File not found"),
	MISSING_REQUIRED_PARAMS(HttpStatus.BAD_REQUEST, "Missing required parameters"),

	/* ==================== FOLDER ==================== */
	CREATE_FOLDER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot create upload folder"),
	FOLDER_INVALID(HttpStatus.BAD_REQUEST, "Invalid folder path"),
	FOLDER_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "No permission to access folder"),

	/* ==================== SECURITY FILE UPLOAD ==================== */
	FILE_NAME_PATH_TRAVERSAL(HttpStatus.BAD_REQUEST, "File name contains invalid path sequence"),
	FILE_SUSPICIOUS_CONTENT(HttpStatus.BAD_REQUEST, "Suspicious file content detected");

	private final HttpStatus status;
	private final String message;

	@Override
	public String getCode() {
		return this.name(); // Trả về tên enum (ví dụ: USER_NOT_FOUND)
	}
}