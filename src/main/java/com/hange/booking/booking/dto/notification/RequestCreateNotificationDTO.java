package com.hange.booking.booking.dto.notification;

import com.hange.booking.booking.entity.constant.NotificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RequestCreateNotificationDTO {

	@NotNull(message = "USER_ID_REQUIRED")
	private Long userId;

	@NotNull(message = "BOOKING_ID_REQUIRED")
	private Long bookingId;

	@NotNull(message = "NOTIFICATION_TYPE_REQUIRED")
	private NotificationType type;

	@NotBlank(message = "TITLE_REQUIRED")
	@Size(max = 255, message = "TITLE_TOO_LONG")
	private String title;

	@NotBlank(message = "MESSAGE_REQUIRED")
	@Size(max = 2000, message = "MESSAGE_TOO_LONG")
	private String message;
}