package com.hange.booking.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.hange.booking.auth.utils.FormatResponse.ApiResponseFormat;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseUtil;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponseFormat> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

		String message = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();

		return ResponseEntity.badRequest().body(ApiResponseUtil.error(message, 400, "DATA_INTEGRITY_VIOLATION"));
	}

	@ExceptionHandler(AppRuntimeException.class)
	public ResponseEntity<ApiResponseFormat> handleAppException(AppRuntimeException ex) {

		ErrorCode code = ex.getErrorCode();

		return ResponseEntity.status(code.getStatus())
				.body(ApiResponseUtil.error(code.getMessage(), code.getStatus().value(), code.getCode()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponseFormat> handleValidationException(MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors().forEach(error -> {
			errors.put(error.getField(), error.getDefaultMessage());
		});

		return ResponseEntity.badRequest()
				.body(ApiResponseUtil.error(errors, 400, ErrorAuthCode.VALIDATION_FAILED.name()));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ApiResponseFormat> handleMaxSizeException(MaxUploadSizeExceededException ex) {

		return ResponseEntity.badRequest().body(
				ApiResponseUtil.error("File upload vượt quá dung lượng cho phép (tối đa 50MB)", 400, "FILE_TOO_LARGE"));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponseFormat> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

		return ResponseEntity.badRequest().body(ApiResponseUtil.error("Request body is missing or malformed", 400,
				ErrorAuthCode.VALIDATION_FAILED.name()));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponseFormat> handleMissingServletRequestParameterException(
			MissingServletRequestParameterException ex) {

		String message = String.format("Missing required parameter: %s", ex.getParameterName());

		return ResponseEntity.badRequest().body(
				ApiResponseUtil.error(message, 400, ErrorAuthCode.VALIDATION_MISSING_REQUIRED_PARAM_FIELD.name()));
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ApiResponseFormat> handleHttpMediaTypeNotSupportedException(
			HttpMediaTypeNotSupportedException ex) {

		return ResponseEntity.status(415)
				.body(ApiResponseUtil.error("Unsupported content type", 415, "UNSUPPORTED_MEDIA_TYPE"));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponseFormat> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException ex) {

		String message = String.format("Invalid value for parameter: %s", ex.getName());

		return ResponseEntity.badRequest()
				.body(ApiResponseUtil.error(message, 400, ErrorAuthCode.VALIDATION_FAILED.name()));
	}
//
//	// Xử lý tất cả các exception khác
//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<ApiResponseFormat> handleAllExceptions(Exception ex) {
//		int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
//		String message = ex.getMessage();
//		System.out.println("Error at fallback @ExceptionHandler: " + message);
//		return ResponseEntity.internalServerError().body(ApiResponseUtil.error("Internal server error", status));
//	}
}