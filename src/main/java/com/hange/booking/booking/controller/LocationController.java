package com.hange.booking.booking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hange.booking.auth.utils.FormatResponse.ApiResponseFormat;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseUtil;
import com.hange.booking.booking.dto.location.RequestCreateLocationDTO;
import com.hange.booking.booking.dto.location.RequestUpdateLocationDTO;
import com.hange.booking.booking.dto.location.ResponseLocationDTO;
import com.hange.booking.booking.service.LocationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

	private final LocationService locationService;

	@GetMapping
	public ResponseEntity<ApiResponseFormat> getAll() {

		List<ResponseLocationDTO> response = locationService.getAll();

		return ResponseEntity.ok(ApiResponseUtil.success(response, HttpStatus.OK.value()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> getById(@PathVariable(name = "id", required = true) Long id) {

		return ResponseEntity.ok(ApiResponseUtil.success(locationService.getById(id), HttpStatus.OK.value()));
	}

	@GetMapping("/slug/{slug}")
	public ResponseEntity<ApiResponseFormat> getBySlug(@PathVariable(name = "slug") String slug) {

		return ResponseEntity.ok(ApiResponseUtil.success(locationService.getBySlug(slug), HttpStatus.OK.value()));
	}

	@PostMapping
	public ResponseEntity<ApiResponseFormat> create(@Valid @RequestBody RequestCreateLocationDTO request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponseUtil.success(locationService.create(request), HttpStatus.CREATED.value()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> update(@PathVariable(name = "id", required = true) Long id,
			@RequestBody RequestUpdateLocationDTO request) {

		return ResponseEntity.ok(ApiResponseUtil.success(locationService.update(id, request), HttpStatus.OK.value()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> delete(@PathVariable(name = "id", required = true) Long id) {

		locationService.delete(id);

		return ResponseEntity.ok(ApiResponseUtil.success("Delete location success", HttpStatus.OK.value()));
	}
}