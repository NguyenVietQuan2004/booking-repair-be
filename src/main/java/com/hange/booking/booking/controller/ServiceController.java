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
import com.hange.booking.booking.dto.service.RequestCreateServiceDTO;
import com.hange.booking.booking.dto.service.RequestUpdateServiceDTO;
import com.hange.booking.booking.dto.service.ResponseServiceDTO;
import com.hange.booking.booking.service.ServiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

	private final ServiceService serviceService;

	@GetMapping
	public ResponseEntity<ApiResponseFormat> getAll() {
		List<ResponseServiceDTO> response = serviceService.getAll();
		return ResponseEntity.ok().body(ApiResponseUtil.success(response, HttpStatus.OK.value()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> getById(@PathVariable(name = "id", required = true) Long id) {

		ResponseServiceDTO response = serviceService.getById(id);

		return ResponseEntity.ok(ApiResponseUtil.success(response, HttpStatus.OK.value()));
	}

	@GetMapping("/slug/{slug}")
	public ResponseEntity<ApiResponseFormat> getBySlug(@PathVariable(name = "slug") String slug) {

		ResponseServiceDTO response = serviceService.getBySlug(slug);

		return ResponseEntity.ok(ApiResponseUtil.success(response, HttpStatus.OK.value()));
	}

	@PostMapping
	public ResponseEntity<ApiResponseFormat> create(@Valid @RequestBody RequestCreateServiceDTO request) {

		ResponseServiceDTO response = serviceService.create(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponseUtil.success(response, HttpStatus.CREATED.value()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> update(@PathVariable(name = "id", required = true) Long id,
			@RequestBody RequestUpdateServiceDTO request) {

		ResponseServiceDTO response = serviceService.update(id, request);

		return ResponseEntity.ok().body(ApiResponseUtil.success(response, HttpStatus.OK.value()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> delete(@PathVariable(name = "id", required = true) Long id) {

		serviceService.delete(id);

		return ResponseEntity.ok().body(ApiResponseUtil.success("Delete service success", HttpStatus.OK.value()));
	}
}