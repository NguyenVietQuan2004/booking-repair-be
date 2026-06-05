package com.hange.booking.booking.dto.notification;

import java.time.LocalDateTime;

import com.hange.booking.booking.entity.constant.NotificationType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseNotificationDTO {

	private Long id;

	private Long userId;

	private Long bookingId;

	private NotificationType type;

	private String title;

	private String message;

	private Boolean isRead;

	private LocalDateTime createdAt;
}