package com.hange.booking.booking.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hange.booking.booking.dto.location.RequestCreateLocationDTO;
import com.hange.booking.booking.dto.location.RequestUpdateLocationDTO;
import com.hange.booking.booking.dto.location.ResponseLocationDTO;
import com.hange.booking.booking.entity.Location;
import com.hange.booking.booking.repository.LocationRepository;
import com.hange.booking.booking.service.LocationService;
import com.hange.booking.booking.utils.mapper.LocationMapper;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorBookingCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

	private final LocationRepository locationRepository;
	private final LocationMapper locationMapper;

	@Override
	public List<ResponseLocationDTO> getAll() {
		return locationRepository.findAll().stream().map(locationMapper::toDTO).toList();
	}

	@Override
	public ResponseLocationDTO getBySlug(String slug) {

		Location location = locationRepository.findBySlug(slug)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.LOCATION_NOT_FOUND));

		return locationMapper.toDTO(location);
	}

	@Override
	public ResponseLocationDTO getById(Long id) {

		Location location = locationRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.LOCATION_NOT_FOUND));

		return locationMapper.toDTO(location);
	}

	@Override
	@Transactional
	public ResponseLocationDTO create(RequestCreateLocationDTO request) {

		String slug = request.getSlug().trim();

		if (locationRepository.existsBySlug(slug)) {
			throw new AppRuntimeException(ErrorBookingCode.LOCATION_SLUG_ALREADY_EXISTS);
		}
		if (request.getOpenTime() != null && request.getCloseTime() != null
				&& !request.getOpenTime().isBefore(request.getCloseTime())) {

			throw new AppRuntimeException(ErrorBookingCode.LOCATION_TIME_INVALID);
		}
		Location location = locationMapper.toEntity(request);

		Location saved = locationRepository.save(location);

		return locationMapper.toDTO(saved);
	}

	@Override
	@Transactional
	public ResponseLocationDTO update(Long id, RequestUpdateLocationDTO request) {

		Location location = locationRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.LOCATION_NOT_FOUND));

		String slug = request.getSlug() != null ? request.getSlug().trim() : null;

		if (slug != null && !slug.equals(location.getSlug()) && locationRepository.existsBySlug(slug)) {

			throw new AppRuntimeException(ErrorBookingCode.LOCATION_SLUG_ALREADY_EXISTS);
		}
		if (request.getOpenTime() != null && request.getCloseTime() != null
				&& !request.getOpenTime().isBefore(request.getCloseTime())) {

			throw new AppRuntimeException(ErrorBookingCode.LOCATION_TIME_INVALID);
		}
		locationMapper.update(location, request);

		Location saved = locationRepository.save(location);

		return locationMapper.toDTO(saved);
	}

	@Override
	@Transactional
	public void delete(Long id) {

		Location location = locationRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.LOCATION_NOT_FOUND));

		locationRepository.delete(location);
	}
}