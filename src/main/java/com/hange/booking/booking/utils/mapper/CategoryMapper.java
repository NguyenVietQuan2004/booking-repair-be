package com.hange.booking.booking.utils.mapper;

import org.springframework.stereotype.Component;

import com.hange.booking.booking.dto.categoy.RequestCreateCategoryDTO;
import com.hange.booking.booking.dto.categoy.RequestUpdateCategoryDTO;
import com.hange.booking.booking.dto.categoy.ResponseCategoryDTO;
import com.hange.booking.booking.dto.image.ResponseImageDTO;
import com.hange.booking.booking.entity.Category;

@Component
public class CategoryMapper {

	public Category toEntity(RequestCreateCategoryDTO request) {

		if (request == null) {
			return null;
		}

		return Category.builder().name(request.getName().trim()).slug(request.getSlug().trim())
				.description(request.getDescription()).content(request.getContent()).build();
	}

	public ResponseCategoryDTO toDTO(Category category) {

		if (category == null) {
			return null;
		}

		ResponseCategoryDTO response = new ResponseCategoryDTO();

		response.setId(category.getId());
		response.setName(category.getName());
		response.setSlug(category.getSlug());
		response.setDescription(category.getDescription());
		response.setContent(category.getContent());

		response.setImages(category.getImages().stream().map(image -> ResponseImageDTO.builder()
				.id(image.getId()).url(image.getUrl()).description(image.getDescription()).build()).toList());

		response.setCreatedAt(category.getCreatedAt());
		response.setUpdatedAt(category.getUpdatedAt());

		return response;
	}

	public void updateCategory(Category category, RequestUpdateCategoryDTO request) {

		if (request.getName() != null) {
			category.setName(request.getName().trim());
		}

		if (request.getSlug() != null) {
			category.setSlug(request.getSlug().trim());
		}

		if (request.getDescription() != null) {
			category.setDescription(request.getDescription());
		}

		if (request.getContent() != null) {
			category.setContent(request.getContent());
		}
	}
}