package com.hange.booking.auth.config;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403

		String message = accessDeniedException.getMessage() != null ? accessDeniedException.getMessage() : "Forbidden";
		objectMapper.writeValue(response.getWriter(),
				ApiResponseUtil.error(message, HttpServletResponse.SC_FORBIDDEN, "ACCESS_DENIED"));

	}
}
