package com.hange.booking.booking.dto.categoy;

import java.time.LocalDateTime;
import java.util.List;

import com.hange.booking.booking.dto.image.ResponseImageDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseCategoryDTO {

	private Long id;

	private String name;

	private String slug;

	private String description;

	private String content;

	private List<ResponseImageDTO> images;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}