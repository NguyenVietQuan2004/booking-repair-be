package com.hange.booking.booking.service.impl;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.hange.booking.booking.entity.SocketMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SocketServiceImpl {

	private final SimpMessagingTemplate messagingTemplate;

	// gửi cho 1 user
	public void sendToUser(Long userId, SocketMessage message) {

		messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", message);
	}

//	// gửi cho admin
//	public void sendToAdmin(SocketMessage message) {
//
//		messagingTemplate.convertAndSend("/topic/admin", message);
//	}
}