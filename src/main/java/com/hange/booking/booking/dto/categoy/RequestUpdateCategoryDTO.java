package com.hange.booking.booking.dto.categoy;

import java.util.List;

import com.hange.booking.booking.dto.image.RequestImageDTO;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUpdateCategoryDTO {

	private String name;

	@Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be URL friendly")
	private String slug;

	private String description;

	private String content;

	private List<RequestImageDTO> images;
}