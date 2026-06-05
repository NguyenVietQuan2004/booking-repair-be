package com.hange.booking.booking.utils.mapper;

import org.springframework.stereotype.Component;

import com.hange.booking.booking.dto.location.RequestCreateLocationDTO;
import com.hange.booking.booking.dto.location.RequestUpdateLocationDTO;
import com.hange.booking.booking.dto.location.ResponseLocationDTO;
import com.hange.booking.booking.entity.Location;

@Component
public class LocationMapper {

	public Location toEntity(RequestCreateLocationDTO request) {

		if (request == null)
			return null;

		return Location.builder().name(request.getName().trim()).slug(request.getSlug().trim())
				.address(request.getAddress()).phone(request.getPhone()).mapEmbedUrl(request.getMapEmbedUrl())
				.openTime(request.getOpenTime()).closeTime(request.getCloseTime()).build();
	}

	public ResponseLocationDTO toDTO(Location location) {

		if (location == null)
			return null;

		ResponseLocationDTO dto = new ResponseLocationDTO();

		dto.setId(location.getId());
		dto.setName(location.getName());
		dto.setSlug(location.getSlug());
		dto.setAddress(location.getAddress());
		dto.setPhone(location.getPhone());
		dto.setMapEmbedUrl(location.getMapEmbedUrl());
		dto.setOpenTime(location.getOpenTime());
		dto.setCloseTime(location.getCloseTime());
		dto.setCreatedAt(location.getCreatedAt());
		dto.setUpdatedAt(location.getUpdatedAt());

		return dto;
	}

	public void update(Location location, RequestUpdateLocationDTO request) {

		if (request.getName() != null) {
			location.setName(request.getName().trim());
		}

		if (request.getSlug() != null) {
			location.setSlug(request.getSlug().trim());
		}

		if (request.getAddress() != null) {
			location.setAddress(request.getAddress());
		}

		if (request.getPhone() != null) {
			location.setPhone(request.getPhone());
		}

		if (request.getMapEmbedUrl() != null) {
			location.setMapEmbedUrl(request.getMapEmbedUrl());
		}

		if (request.getOpenTime() != null) {
			location.setOpenTime(request.getOpenTime());
		}

		if (request.getCloseTime() != null) {
			location.setCloseTime(request.getCloseTime());
		}
	}
}