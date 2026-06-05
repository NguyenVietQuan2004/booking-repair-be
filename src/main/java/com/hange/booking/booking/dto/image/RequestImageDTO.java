package com.hange.booking.booking.dto.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestImageDTO {

	@NotBlank(message = "URL_REQUIRED")
	@Size(max = 1000, message = "URL_TOO_LONG")
	private String url;

	@Size(max = 500, message = "DESCRIPTION_TOO_LONG")
	private String description;
}