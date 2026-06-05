package com.hange.booking.booking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocketMessage {

	private Object event;
	private Object data;
}