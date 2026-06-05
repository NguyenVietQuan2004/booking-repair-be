package com.hange.booking.booking.dto.service;

import java.math.BigDecimal;
import java.util.List;

import com.hange.booking.booking.dto.image.RequestImageDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUpdateServiceDTO {

	private String name;

	@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be URL friendly")
	private String slug;

	private String description;

	private String content;
	@Positive(message = "Duration must be greater than 0")
	private Integer durationMinutes;

	@Positive(message = "Price must be greater than 0")
	@DecimalMin(value = "0.0")
	private BigDecimal price;

	private Long categoryId;

	private List<RequestImageDTO> images;
}