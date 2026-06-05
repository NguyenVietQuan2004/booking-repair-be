package com.hange.booking.booking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hange.booking.auth.utils.FormatResponse.ApiResponseFormat;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseUtil;
import com.hange.booking.booking.dto.categoy.RequestCreateCategoryDTO;
import com.hange.booking.booking.dto.categoy.RequestUpdateCategoryDTO;
import com.hange.booking.booking.dto.categoy.ResponseCategoryDTO;
import com.hange.booking.booking.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	public ResponseEntity<ApiResponseFormat> getAllCategories() {

		List<ResponseCategoryDTO> response = categoryService.getAllCategories();

		return ResponseEntity.ok().body(ApiResponseUtil.success(response, HttpStatus.OK.value()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> getCategoryById(@PathVariable(name = "id", required = true) Long id) {

		ResponseCategoryDTO response = categoryService.getCategoryById(id);

		return ResponseEntity.ok(ApiResponseUtil.success(response, HttpStatus.OK.value()));
	}

	@GetMapping("/slug/{slug}")
	public ResponseEntity<ApiResponseFormat> getCategoryBySlug(
			@PathVariable(name = "slug", required = true) String slug) {

		ResponseCategoryDTO response = categoryService.getCategoryBySlug(slug);

		return ResponseEntity.ok(ApiResponseUtil.success(response, HttpStatus.OK.value()));
	}

	@PostMapping
	public ResponseEntity<ApiResponseFormat> createCategory(@Valid @RequestBody RequestCreateCategoryDTO request) {

		ResponseCategoryDTO response = categoryService.createCategory(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponseUtil.success(response, HttpStatus.CREATED.value()));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> updateCategory(@PathVariable(name = "id", required = true) Long id,
			@RequestBody RequestUpdateCategoryDTO request) {

		ResponseCategoryDTO response = categoryService.updateCategory(id, request);

		return ResponseEntity.ok().body(ApiResponseUtil.success(response, HttpStatus.OK.value()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> replaceCategory(@PathVariable(name = "id", required = true) Long id,
			@RequestBody RequestUpdateCategoryDTO request) {

		ResponseCategoryDTO response = categoryService.replaceCategory(id, request);

		return ResponseEntity.ok(ApiResponseUtil.success(response, HttpStatus.OK.value()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> deleteCategory(@PathVariable(name = "id", required = true) Long id) {

		categoryService.deleteCategory(id);

		return ResponseEntity.ok().body(ApiResponseUtil.success("Delete category success", HttpStatus.OK.value()));
	}
}