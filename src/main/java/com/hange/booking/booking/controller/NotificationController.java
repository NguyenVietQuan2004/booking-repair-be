package com.hange.booking.booking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hange.booking.auth.utils.FormatResponse.ApiResponseFormat;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseUtil;
import com.hange.booking.booking.dto.notification.RequestCreateNotificationDTO;
import com.hange.booking.booking.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	// ===================== GET ALL =====================
	@GetMapping
	public ResponseEntity<ApiResponseFormat> getAll() {

		return ResponseEntity.ok(ApiResponseUtil.success(notificationService.getAll(), HttpStatus.OK.value()));
	}

	// ===================== GET BY USER =====================
	@GetMapping("/user/{userId}")
	public ResponseEntity<ApiResponseFormat> getByUser(@PathVariable(name = "userId", required = true) Long userId) {

		return ResponseEntity.ok(ApiResponseUtil.success(notificationService.getByUser(userId), HttpStatus.OK.value()));
	}

	// ===================== CREATE =====================
	@PostMapping
	public ResponseEntity<ApiResponseFormat> create(@RequestBody RequestCreateNotificationDTO request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponseUtil.success(notificationService.create(request), HttpStatus.CREATED.value()));
	}

	// ===================== MARK AS READ =====================
	@PatchMapping("/{id}/read")
	public ResponseEntity<ApiResponseFormat> markAsRead(@PathVariable(name = "id", required = true) Long id) {

		return ResponseEntity.ok(ApiResponseUtil.success(notificationService.markAsRead(id), HttpStatus.OK.value()));
	}

	// ===================== MARK ALL READ =====================
	@PatchMapping("/user/{userId}/read-all")
	public ResponseEntity<ApiResponseFormat> markAllRead(@PathVariable(name = "userId", required = true) Long userId) {

		notificationService.markAllRead(userId);

		return ResponseEntity.ok(ApiResponseUtil.success("All notifications marked as read", HttpStatus.OK.value()));
	}

	// ===================== DELETE =====================
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> delete(@PathVariable(name = "id", required = true) Long id) {

		notificationService.delete(id);

		return ResponseEntity.ok(ApiResponseUtil.success("Delete notification success", HttpStatus.OK.value()));
	}
}