package com.hange.booking.common.config;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Component
public class UserHandshakeHandler extends DefaultHandshakeHandler {

	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {

		// TODO: lấy userId từ JWT (đơn giản demo)
		String query = request.getURI().getQuery();

		String userId = null;

		if (query != null && query.contains("userId=")) {
			userId = query.split("userId=")[1];
		}

		System.out.println("Handshake ok: id = " + userId);

		return new StompPrincipal(userId);
	}
}