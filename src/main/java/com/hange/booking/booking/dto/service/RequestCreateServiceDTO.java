package com.hange.booking.booking.dto.service;

import java.math.BigDecimal;
import java.util.List;

import com.hange.booking.booking.dto.image.RequestImageDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCreateServiceDTO {

	@NotBlank(message = "Name is required")
	@Size(max = 255)
	private String name;

	@NotBlank(message = "Slug is required")
	@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be URL friendly")
	@Size(max = 255)
	private String slug;

	private String description;

	private String content;

	@NotNull(message = "Duration is required")
	@Positive(message = "Duration must be greater than 0")
	private Integer durationMinutes;

	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.0")
	@Positive(message = "Duration must be greater than 0")
	private BigDecimal price;

	@NotNull(message = "Category is required")
	private Long categoryId;

	private List<RequestImageDTO> images;
}