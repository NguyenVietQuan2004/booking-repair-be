package com.hange.booking.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

	private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);

	@EventListener
	public void handleConnect(SessionConnectEvent event) {

		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

		System.out.println("WS CONNECT sessionId=" + accessor.getSessionId());
		System.out.println("WS userId=" + (accessor.getUser() != null ? accessor.getUser().getName() : "NULL"));
	}

	@EventListener
	public void handleDisconnect(SessionDisconnectEvent event) {

		System.out.println("WS DISCONNECT sessionId=" + event.getSessionId());
	}
}