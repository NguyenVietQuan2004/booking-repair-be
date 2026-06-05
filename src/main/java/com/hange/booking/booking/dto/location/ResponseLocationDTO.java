package com.hange.booking.booking.dto.location;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseLocationDTO {

	private Long id;

	private String name;

	private String slug;

	private String address;

	private String phone;

	private String mapEmbedUrl;

	private LocalTime openTime;

	private LocalTime closeTime;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}