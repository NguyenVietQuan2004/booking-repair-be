package com.hange.booking.booking.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hange.booking.auth.entity.user.User;
import com.hange.booking.auth.repository.UserRepository;
import com.hange.booking.booking.dto.notification.RequestCreateNotificationDTO;
import com.hange.booking.booking.dto.notification.ResponseNotificationDTO;
import com.hange.booking.booking.entity.Booking;
import com.hange.booking.booking.entity.Notification;
import com.hange.booking.booking.repository.BookingRepository;
import com.hange.booking.booking.repository.NotificationRepository;
import com.hange.booking.booking.service.NotificationService;
import com.hange.booking.booking.utils.mapper.NotificationMapper;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorAuthCode;
import com.hange.booking.common.exception.ErrorBookingCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final BookingRepository bookingRepository;
	private final NotificationMapper notificationMapper;

	@Override
	public List<ResponseNotificationDTO> getAll() {

		return notificationRepository.findAll().stream().map(notificationMapper::toDTO).toList();
	}

	@Override
	public List<ResponseNotificationDTO> getByUser(Long userId) {

		return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(notificationMapper::toDTO)
				.toList();
	}

	@Override
	@Transactional
	public ResponseNotificationDTO create(RequestCreateNotificationDTO request) {

		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new AppRuntimeException(ErrorAuthCode.USER_NOT_FOUND));

		Booking booking = null;

		if (request.getBookingId() != null) {
			booking = bookingRepository.findById(request.getBookingId())
					.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.BOOKING_NOT_FOUND));
		}

		Notification notification = Notification.builder().user(user).booking(booking).type(request.getType())
				.title(request.getTitle()).message(request.getMessage()).isRead(false).build();

		return notificationMapper.toDTO(notificationRepository.save(notification));
	}

	@Override
	@Transactional
	public ResponseNotificationDTO markAsRead(Long id) {

		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.NOTIFICATION_NOT_FOUND));

		notification.setIsRead(true);

		return notificationMapper.toDTO(notificationRepository.save(notification));
	}

	@Override
	@Transactional
	public void markAllRead(Long userId) {

		List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);

		notifications.forEach(n -> n.setIsRead(true));
	}

	@Override
	@Transactional
	public void delete(Long id) {

		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.NOTIFICATION_NOT_FOUND));

		notificationRepository.delete(notification);
	}
}