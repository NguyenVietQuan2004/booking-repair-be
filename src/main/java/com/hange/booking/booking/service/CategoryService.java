package com.hange.booking.booking.service;

import java.util.List;

import com.hange.booking.booking.dto.categoy.RequestCreateCategoryDTO;
import com.hange.booking.booking.dto.categoy.RequestUpdateCategoryDTO;
import com.hange.booking.booking.dto.categoy.ResponseCategoryDTO;

public interface CategoryService {

	List<ResponseCategoryDTO> getAllCategories();

	ResponseCategoryDTO getCategoryById(Long id);

	ResponseCategoryDTO getCategoryBySlug(String slug);

	ResponseCategoryDTO createCategory(RequestCreateCategoryDTO request);

	ResponseCategoryDTO replaceCategory(Long id, RequestUpdateCategoryDTO request);

	ResponseCategoryDTO updateCategory(Long id, RequestUpdateCategoryDTO request);

	void deleteCategory(Long id);
}