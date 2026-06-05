package com.hange.booking.booking.service;

import java.util.List;

import com.hange.booking.booking.dto.notification.RequestCreateNotificationDTO;
import com.hange.booking.booking.dto.notification.ResponseNotificationDTO;

public interface NotificationService {

	List<ResponseNotificationDTO> getAll();

	List<ResponseNotificationDTO> getByUser(Long userId);

	ResponseNotificationDTO create(RequestCreateNotificationDTO request);

	ResponseNotificationDTO markAsRead(Long id);

	void markAllRead(Long userId);

	void delete(Long id);
}