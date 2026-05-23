package com.hange.booking.auth.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hange.booking.auth.exception.AppRuntimeException;
import com.hange.booking.auth.exception.ErrorCode;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationEntryPointJwt implements org.springframework.security.web.AuthenticationEntryPoint {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		Throwable cause = authException.getCause();

		String message = (cause != null && cause.getMessage() != null) ? cause.getMessage()
				: authException.getMessage();
		ErrorCode errorCode = ErrorCode.TOKEN_INVALID;
		if (cause instanceof AppRuntimeException ex) {
			errorCode = ex.getErrorCode();
		}
		objectMapper.writeValue(response.getWriter(),
				ApiResponseUtil.error(message, HttpServletResponse.SC_UNAUTHORIZED, errorCode.name()));
	}
}
