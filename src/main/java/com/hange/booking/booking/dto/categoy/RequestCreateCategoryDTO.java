package com.hange.booking.booking.dto.categoy;

import java.util.List;

import com.hange.booking.booking.dto.image.RequestImageDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCreateCategoryDTO {

	@NotBlank(message = "Name is required")
	@Size(max = 255)
	private String name;

	@NotBlank(message = "Slug is required")
	@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be URL friendly")
	@Size(max = 255)
	private String slug;

	private String description;

	private String content;

	private List<RequestImageDTO> images;
}