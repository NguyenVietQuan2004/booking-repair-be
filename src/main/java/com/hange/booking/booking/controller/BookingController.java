package com.hange.booking.booking.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hange.booking.auth.utils.FormatResponse.ApiResponseFormat;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseUtil;
import com.hange.booking.booking.dto.booking.BookingFilterDTO;
import com.hange.booking.booking.dto.booking.RequestCreateBookingDTO;
import com.hange.booking.booking.dto.booking.ResponseBookingDTO;
import com.hange.booking.booking.service.BookingService;
import com.hange.booking.common.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;

	@PostMapping
	public ResponseEntity<ApiResponseFormat> create(@RequestBody RequestCreateBookingDTO request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponseUtil.success(bookingService.create(request), 201));
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponseFormat> myBookings() {

		return ResponseEntity.ok(ApiResponseUtil.success(bookingService.getMyBookings(), 200));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> getById(@PathVariable(name = "id", required = true) Long id) {

		return ResponseEntity.ok(ApiResponseUtil.success(bookingService.getById(id), 200));
	}

	@GetMapping
	public ResponseEntity<ApiResponseFormat> getAll(BookingFilterDTO filter,
			@PageableDefault(size = 20) Pageable pageable) {

		Page<ResponseBookingDTO> bookings = bookingService.getAll(filter, pageable);

		return ResponseEntity.ok(ApiResponseUtil.success(PageResponse.from(bookings), 200));
	}

	@PatchMapping("/{bookingId}/confirm")
	public ResponseEntity<ApiResponseFormat> confirm(
			@PathVariable(name = "bookingId", required = true) Long bookingId) {

		return ResponseEntity.ok(ApiResponseUtil.success(bookingService.confirm(bookingId), 200));
	}

	@PatchMapping("/{bookingId}/reject")
	public ResponseEntity<ApiResponseFormat> reject(@PathVariable(name = "bookingId", required = true) Long bookingId) {

		return ResponseEntity.ok(ApiResponseUtil.success(bookingService.reject(bookingId), 200));
	}

	@PatchMapping("/{bookingId}/cancel")
	public ResponseEntity<ApiResponseFormat> cancel(@PathVariable(name = "bookingId", required = true) Long bookingId) {

		return ResponseEntity.ok(ApiResponseUtil.success(bookingService.cancel(bookingId), 200));
	}
}