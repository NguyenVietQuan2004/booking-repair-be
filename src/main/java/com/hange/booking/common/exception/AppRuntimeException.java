package com.hange.booking.common.exception;

public class AppRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final ErrorCode errorCode; // ← Đổi thành ErrorCode (interface)

	public AppRuntimeException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}