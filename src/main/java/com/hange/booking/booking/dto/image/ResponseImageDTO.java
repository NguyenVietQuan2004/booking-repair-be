package com.hange.booking.booking.dto.image;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseImageDTO {

	private Long id;

	private String url;

	private String description;
}