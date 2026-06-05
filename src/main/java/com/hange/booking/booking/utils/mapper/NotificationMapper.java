package com.hange.booking.booking.utils.mapper;

import org.springframework.stereotype.Component;

import com.hange.booking.booking.dto.notification.ResponseNotificationDTO;
import com.hange.booking.booking.entity.Notification;

@Component
public class NotificationMapper {

	public ResponseNotificationDTO toDTO(Notification notification) {

		if (notification == null)
			return null;

		ResponseNotificationDTO dto = new ResponseNotificationDTO();

		dto.setId(notification.getId());
		dto.setUserId(notification.getUser().getId());
		dto.setBookingId(notification.getBooking() != null ? notification.getBooking().getId() : null);
		dto.setType(notification.getType());
		dto.setTitle(notification.getTitle());
		dto.setMessage(notification.getMessage());
		dto.setIsRead(notification.getIsRead());
		dto.setCreatedAt(notification.getCreatedAt());

		return dto;
	}
}