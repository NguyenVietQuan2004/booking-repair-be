package com.hange.booking.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hange.booking.booking.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

	long countByUserIdAndIsReadFalse(Long userId);

	List<Notification> findByUserIdAndIsReadFalse(Long userId);
}