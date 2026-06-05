package com.hange.booking.booking.utils.mapper;

import org.springframework.stereotype.Component;

import com.hange.booking.booking.dto.image.ResponseImageDTO;
import com.hange.booking.booking.dto.service.RequestCreateServiceDTO;
import com.hange.booking.booking.dto.service.RequestUpdateServiceDTO;
import com.hange.booking.booking.dto.service.ResponseServiceDTO;
import com.hange.booking.booking.entity.Service;

@Component
public class ServiceMapper {

	public Service toEntity(RequestCreateServiceDTO request) {

		if (request == null) {
			return null;
		}

		return Service.builder().name(request.getName().trim()).slug(request.getSlug().trim())
				.description(request.getDescription()).content(request.getContent())
				.durationMinutes(request.getDurationMinutes()).price(request.getPrice()).build();
	}

	public ResponseServiceDTO toDTO(Service service) {

		if (service == null) {
			return null;
		}

		ResponseServiceDTO response = new ResponseServiceDTO();

		response.setId(service.getId());
		response.setName(service.getName());
		response.setSlug(service.getSlug());
		response.setDescription(service.getDescription());
		response.setContent(service.getContent());
		response.setDurationMinutes(service.getDurationMinutes());
		response.setPrice(service.getPrice());

		if (service.getCategory() != null) {
			response.setCategoryId(service.getCategory().getId());
			response.setCategoryName(service.getCategory().getName());
		}

		response.setImages(service.getImages().stream().map(image -> ResponseImageDTO.builder().id(image.getId())
				.url(image.getUrl()).description(image.getDescription()).build()).toList());

		response.setCreatedAt(service.getCreatedAt());
		response.setUpdatedAt(service.getUpdatedAt());

		return response;
	}

	public void update(Service service, RequestUpdateServiceDTO request) {

		if (request.getName() != null) {
			service.setName(request.getName().trim());
		}

		if (request.getSlug() != null) {
			service.setSlug(request.getSlug().trim());
		}

		if (request.getDescription() != null) {
			service.setDescription(request.getDescription());
		}

		if (request.getContent() != null) {
			service.setContent(request.getContent());
		}

		if (request.getDurationMinutes() != null) {
			service.setDurationMinutes(request.getDurationMinutes());
		}

		if (request.getPrice() != null) {
			service.setPrice(request.getPrice());
		}
	}
}