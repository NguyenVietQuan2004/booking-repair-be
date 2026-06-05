package com.hange.booking.booking.dto.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.hange.booking.booking.dto.image.ResponseImageDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseServiceDTO {

	private Long id;

	private String name;

	private String slug;

	private String description;

	private String content;

	private Integer durationMinutes;

	private BigDecimal price;

	private Long categoryId;

	private String categoryName;

	private List<ResponseImageDTO> images;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}