package com.hange.booking.booking.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.hange.booking.booking.dto.service.RequestCreateServiceDTO;
import com.hange.booking.booking.dto.service.RequestUpdateServiceDTO;
import com.hange.booking.booking.dto.service.ResponseServiceDTO;
import com.hange.booking.booking.entity.Category;
import com.hange.booking.booking.entity.Image;
import com.hange.booking.booking.entity.Service;
import com.hange.booking.booking.entity.constant.ImageType;
import com.hange.booking.booking.repository.CategoryRepository;
import com.hange.booking.booking.repository.ServiceRepository;
import com.hange.booking.booking.service.ServiceService;
import com.hange.booking.booking.utils.mapper.ServiceMapper;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorBookingCode;

import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

	private final ServiceRepository serviceRepository;
	private final CategoryRepository categoryRepository;
	private final ServiceMapper serviceMapper;

	@Override
	public List<ResponseServiceDTO> getAll() {
		return serviceRepository.findAll().stream().map(serviceMapper::toDTO).toList();
	}

	@Override
	public ResponseServiceDTO getById(Long id) {

		Service service = serviceRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.SERVICE_NOT_FOUND));

		return serviceMapper.toDTO(service);
	}

	@Override
	public ResponseServiceDTO getBySlug(String slug) {

		Service service = serviceRepository.findBySlug(slug)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.SERVICE_NOT_FOUND));

		return serviceMapper.toDTO(service);
	}

	@Override
	@Transactional
	public ResponseServiceDTO create(RequestCreateServiceDTO request) {

		if (serviceRepository.existsBySlug(request.getSlug().trim())) {
			throw new AppRuntimeException(ErrorBookingCode.SERVICE_SLUG_ALREADY_EXISTS);
		}

		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.CATEGORY_NOT_FOUND));

		validateServicePrice(request);

		Service service = serviceMapper.toEntity(request);
		service.setCategory(category);

		if (request.getImages() != null) {

			List<Image> images = request
					.getImages().stream().map(item -> Image.builder().url(item.getUrl())
							.description(item.getDescription()).entityType(ImageType.SERVICE).service(service).build())
					.toList();

			service.setImages(images);
		}

		Service saved = serviceRepository.save(service);

		return serviceMapper.toDTO(saved);
	}

	@Override
	@Transactional
	public ResponseServiceDTO update(Long id, RequestUpdateServiceDTO request) {

		Service service = serviceRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.SERVICE_NOT_FOUND));

		if (request.getSlug() != null && !request.getSlug().trim().equals(service.getSlug())
				&& serviceRepository.existsBySlug(request.getSlug().trim())) {

			throw new AppRuntimeException(ErrorBookingCode.SERVICE_SLUG_ALREADY_EXISTS);
		}

		serviceMapper.update(service, request);

		if (request.getCategoryId() != null) {

			Category category = categoryRepository.findById(request.getCategoryId())
					.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.CATEGORY_NOT_FOUND));

			service.setCategory(category);
		}

		if (request.getDurationMinutes() != null && request.getDurationMinutes() < 0) {
			throw new AppRuntimeException(ErrorBookingCode.INVALID_DURATION);
		}

		if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
			throw new AppRuntimeException(ErrorBookingCode.INVALID_PRICE);
		}

		if (request.getImages() != null) {

			service.getImages().clear();

			List<Image> images = request
					.getImages().stream().map(item -> Image.builder().url(item.getUrl())
							.description(item.getDescription()).entityType(ImageType.SERVICE).service(service).build())
					.toList();

			service.getImages().addAll(images);
		}

		Service saved = serviceRepository.save(service);

		return serviceMapper.toDTO(saved);
	}

	@Override
	@Transactional
	public void delete(Long id) {

		Service service = serviceRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.SERVICE_NOT_FOUND));

		serviceRepository.delete(service);
	}

	private void validateServicePrice(RequestCreateServiceDTO request) {
		if (request.getDurationMinutes() != null && request.getDurationMinutes() < 0) {
			throw new AppRuntimeException(ErrorBookingCode.INVALID_DURATION);
		}

		if (request.getPrice().compareTo(BigDecimal.ZERO) < 0)
			throw new AppRuntimeException(ErrorBookingCode.INVALID_PRICE);
	}

}
