package com.hange.booking.booking.dto.location;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCreateLocationDTO {

	@NotBlank
	@Size(max = 255)
	private String name;

	@NotBlank
	@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
	private String slug;

	private String address;

	private String phone;

	private String mapEmbedUrl;

	private LocalTime openTime;

	private LocalTime closeTime;
}