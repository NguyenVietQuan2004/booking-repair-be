package com.hange.booking.booking.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hange.booking.booking.dto.booking.RequestCreateBookingDTO;
import com.hange.booking.booking.service.BookingService;
import com.hange.booking.common.exception.GlobalExceptionHandler;

@WebMvcTest(controllers = BookingController.class, excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
				com.hange.booking.auth.config.interceptor.PermissionInterceptor.class,
				com.hange.booking.auth.config.interceptor.PermissionInterceptorConfiguration.class, // ← Thêm cái này
				com.hange.booking.common.filter.RateLimitFilter.class,
				com.hange.booking.auth.config.security.SecurityConfiguration.class }) })
@AutoConfigureMockMvc(addFilters = false)

@Import(GlobalExceptionHandler.class)
class BookingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BookingService bookingService;

	@Test
	void shouldCreateBookingSuccessfully() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		RequestCreateBookingDTO request = new RequestCreateBookingDTO();
		request.setSlotId(1L);
		request.setNote("Test note");

		when(bookingService.create(any())).thenReturn(null);

		mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());

		verify(bookingService).create(any());
	}

	@Test
	void shouldFailWhenSlotIdNull() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		RequestCreateBookingDTO request = new RequestCreateBookingDTO();
		request.setSlotId(null);
		request.setNote("Test");

		mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
	}

	@Test
	void shouldFailWhenBodyEmpty() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new RequestCreateBookingDTO())))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn500WhenServiceThrowsException() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		RequestCreateBookingDTO request = new RequestCreateBookingDTO();
		request.setSlotId(1L);

		doThrow(new RuntimeException("DB error")).when(bookingService).create(any());

		mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isInternalServerError());
	}

}