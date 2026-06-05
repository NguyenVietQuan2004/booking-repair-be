
package com.hange.booking.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorBookingCode implements ErrorCode {

	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found"),
	CATEGORY_SLUG_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Category slug already exists"),
	/* ==================== SERVICE ==================== */
	NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Notification not found"),
	BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, "Notification not found"),
	SERVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "Service not found"),
	SERVICE_SLUG_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Service slug already exists"),
	SERVICE_SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "Service slot not found"),
	LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Location not found"),
	LOCATION_TIME_INVALID(HttpStatus.NOT_FOUND, "Location not found"),

	DUPLICATE_SLOT(HttpStatus.BAD_REQUEST, "duplicate slot"),
	INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "Invalid time range"),
	INVALID_MAX_CAPACITY(HttpStatus.BAD_REQUEST, "Capacity is invalid"),
	TIME_OVERLAP(HttpStatus.BAD_REQUEST, "Time overlap"), SLOT_FULL(HttpStatus.CONFLICT, "Slot full"),
	INVALID_BOOKED_COUNT(HttpStatus.CONFLICT, "Invalid booked count"),
	LOCATION_SLUG_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Location slug already exists"),

	BOOKING_TIME_INVALID(HttpStatus.BAD_REQUEST, "Booking time invalid"),

	/* ==================== BOOKING ==================== */
	SLOT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "Slot not available"),
	DUPLICATE_BOOKING(HttpStatus.BAD_REQUEST, "Duplicate booking"),
	BOOKING_STATUS_INVALID(HttpStatus.BAD_REQUEST, "Booking status invalid"),
	BOOKING_RATE_LIMIT(HttpStatus.TOO_MANY_REQUESTS, "Booking rate limit exceeded"),

	// ✅ NEW ERROR CODES
	INVALID_DURATION(HttpStatus.BAD_REQUEST, "Duration must be greater than or equal to 0"),
	INVALID_PRICE(HttpStatus.BAD_REQUEST, "Price must be greater than or equal to 0");

	private final HttpStatus status;
	private final String message;

	@Override
	public String getCode() {
		return this.name();
	}
}