package com.hange.booking.booking.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hange.booking.booking.dto.categoy.RequestCreateCategoryDTO;
import com.hange.booking.booking.dto.categoy.RequestUpdateCategoryDTO;
import com.hange.booking.booking.dto.categoy.ResponseCategoryDTO;
import com.hange.booking.booking.entity.Category;
import com.hange.booking.booking.entity.Image;
import com.hange.booking.booking.entity.constant.ImageType;
import com.hange.booking.booking.repository.CategoryRepository;
import com.hange.booking.booking.service.CategoryService;
import com.hange.booking.booking.utils.mapper.CategoryMapper;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorBookingCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	@Override
	public List<ResponseCategoryDTO> getAllCategories() {

		return categoryRepository.findAll().stream().map(categoryMapper::toDTO).toList();
	}

	@Override
	public ResponseCategoryDTO getCategoryBySlug(String slug) {

		Category category = categoryRepository.findBySlug(slug)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.CATEGORY_NOT_FOUND));

		return categoryMapper.toDTO(category);
	}

	@Override
	public ResponseCategoryDTO getCategoryById(Long id) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.CATEGORY_NOT_FOUND));

		return categoryMapper.toDTO(category);
	}

	@Override
	@Transactional
	public ResponseCategoryDTO createCategory(RequestCreateCategoryDTO request) {

		if (categoryRepository.existsBySlug(request.getSlug())) {
			throw new AppRuntimeException(ErrorBookingCode.CATEGORY_SLUG_ALREADY_EXISTS);
		}

		Category category = categoryMapper.toEntity(request);

		if (request.getImages() != null) {

			List<Image> images = request.getImages().stream().map(item -> Image.builder().url(item.getUrl())
					.description(item.getDescription()).entityType(ImageType.CATEGORY).category(category).build())
					.toList();

			category.setImages(images);
		}

		Category savedCategory = categoryRepository.save(category);
		return categoryMapper.toDTO(savedCategory);
	}

	@Override
	@Transactional
	public ResponseCategoryDTO updateCategory(Long id, RequestUpdateCategoryDTO request) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.CATEGORY_NOT_FOUND));

		if (request.getSlug() != null && !request.getSlug().equals(category.getSlug())
				&& categoryRepository.existsBySlug(request.getSlug())) {

			throw new AppRuntimeException(ErrorBookingCode.CATEGORY_SLUG_ALREADY_EXISTS);
		}

		categoryMapper.updateCategory(category, request);

		if (request.getImages() != null) {

			category.getImages().clear();

			List<Image> images = request.getImages().stream().map(item -> Image.builder().url(item.getUrl())
					.description(item.getDescription()).entityType(ImageType.CATEGORY).category(category).build())
					.toList();

			category.getImages().addAll(images);
		}

		Category savedCategory = categoryRepository.save(category);

		return categoryMapper.toDTO(savedCategory);
	}

	@Override
	@Transactional
	public ResponseCategoryDTO replaceCategory(Long id, RequestUpdateCategoryDTO request) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.CATEGORY_NOT_FOUND));

		// slug check (nếu có gửi lên)
		if (request.getSlug() != null && !request.getSlug().equals(category.getSlug())
				&& categoryRepository.existsBySlug(request.getSlug())) {

			throw new AppRuntimeException(ErrorBookingCode.CATEGORY_SLUG_ALREADY_EXISTS);
		}

		// =========================
		// MERGE LOGIC (PUT kiểu bạn muốn)
		// =========================

		if (request.getName() != null) {
			category.setName(request.getName());
		}

		if (request.getSlug() != null) {
			category.setSlug(request.getSlug());
		}

		if (request.getDescription() != null) {
			category.setDescription(request.getDescription());
		}

		if (request.getContent() != null) {
			category.setContent(request.getContent());
		}

		// =========================
		// IMAGES (replace nếu có gửi)
		// =========================
		if (request.getImages() != null) {

			category.getImages().clear();

			List<Image> images = request.getImages().stream().map(item -> Image.builder().url(item.getUrl())
					.description(item.getDescription()).entityType(ImageType.CATEGORY).category(category).build())
					.toList();

			category.getImages().addAll(images);
		}

		Category saved = categoryRepository.save(category);

		return categoryMapper.toDTO(saved);
	}

	@Override
	@Transactional
	public void deleteCategory(Long id) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.CATEGORY_NOT_FOUND));

		categoryRepository.delete(category);
	}
}