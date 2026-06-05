package com.hange.booking.booking.dto.location;

import java.time.LocalTime;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUpdateLocationDTO {

	private String name;

	@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
	private String slug;

	private String address;

	private String phone;

	private String mapEmbedUrl;

	private LocalTime openTime;

	private LocalTime closeTime;
}