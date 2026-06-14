package com.hange.booking.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;// ĐÚNG (Spring Boot 3.x / 4.x)// ĐÚNG (Spring Boot 3.x / 4.x)
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hange.booking.auth.controller.auth.AuthController;
import com.hange.booking.auth.dto.user.RequestRegisterDTO;
import com.hange.booking.auth.service.auth.AuthService;
import com.hange.booking.auth.service.user.TokenService;
import com.hange.booking.auth.service.user.UserService;
import com.hange.booking.auth.utils.SecurityUtil;
import com.hange.booking.auth.utils.mapper.UserMapper;
import com.hange.booking.common.exception.GlobalExceptionHandler;
import com.hange.booking.common.filter.RateLimitFilter;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserMapper userMapper;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private TokenService tokenService;

	@MockitoBean
	private SecurityUtil securityUtil;

	@MockitoBean
	private AuthenticationManagerBuilder authenticationManagerBuilder;

	@MockitoBean
	private JwtDecoder jwtDecoder;

	@MockitoBean
	private RateLimitFilter rateLimitFilter;

	@Test
	void shouldRegisterSuccessfully() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		RequestRegisterDTO request = new RequestRegisterDTO();
		request.setEmail("test@gmail.com");
		request.setPassword("Password@123");

		doNothing().when(authService).register(any());

		mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());

		verify(authService).register(any());
	}

	@Test
	void shouldFailWhenEmailBlank() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		RequestRegisterDTO request = new RequestRegisterDTO();
		request.setEmail("");
		request.setPassword("Password@123");

		mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
	}

	@Test
	void shouldFailWhenEmailInvalid() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		RequestRegisterDTO request = new RequestRegisterDTO();
		request.setEmail("invalid-email");
		request.setPassword("Password@123");

		mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
	}

	@Test
	void shouldFailWhenPasswordBlank() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		RequestRegisterDTO request = new RequestRegisterDTO();
		request.setEmail("test@gmail.com");
		request.setPassword("");

		mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
	}

	@Test
	void shouldFailWhenBodyEmpty() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new RequestRegisterDTO()))).andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn500WhenServiceFails() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		RequestRegisterDTO request = new RequestRegisterDTO();
		request.setEmail("test@gmail.com");
		request.setPassword("Password@123");

		doThrow(new RuntimeException("DB error")).when(authService).register(any());

		mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isInternalServerError());
	}
}