package com.hange.booking.booking.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.hange.booking.booking.dto.serviceSlot.RequestBulkCreateServiceSlotDTO;
import com.hange.booking.booking.dto.serviceSlot.RequestCreateServiceSlotDTO;
import com.hange.booking.booking.dto.serviceSlot.RequestUpdateServiceSlotDTO;
import com.hange.booking.booking.dto.serviceSlot.ResponseServiceSlotDTO;
import com.hange.booking.booking.dto.serviceSlot.ServiceSlotFilterDTO;
import com.hange.booking.booking.service.ServiceSlotService;
import com.hange.booking.common.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/service-slots")
@RequiredArgsConstructor
public class ServiceSlotController {

	private final ServiceSlotService serviceSlotService;

	@GetMapping("/service/{serviceId}/locations")
	public ResponseEntity<ApiResponseFormat> getLocationsByService(
			@PathVariable(name = "serviceId", required = true) Long serviceId) {

		return ResponseEntity.ok(ApiResponseUtil.success(serviceSlotService.getLocationsByService(serviceId), 200));
	}

	@GetMapping
	public ResponseEntity<ApiResponseFormat> getAll(ServiceSlotFilterDTO filter,
			@PageableDefault(size = 999) Pageable pageable) {

		Page<ResponseServiceSlotDTO> slots = serviceSlotService.getAll(filter, pageable);

		return ResponseEntity.ok(ApiResponseUtil.success(PageResponse.from(slots), 200));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> getById(@PathVariable(name = "id", required = true) Long id) {
		return ResponseEntity.ok(ApiResponseUtil.success(serviceSlotService.getById(id), 200));
	}

	@GetMapping("/service/{serviceId}")
	public ResponseEntity<ApiResponseFormat> getByService(
			@PathVariable(name = "serviceId", required = true) Long serviceId) {
		return ResponseEntity.ok(ApiResponseUtil.success(serviceSlotService.getByService(serviceId), 200));
	}

	@PostMapping("/bulk")
	public ResponseEntity<ApiResponseFormat> bulkCreate(@RequestBody RequestBulkCreateServiceSlotDTO request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponseUtil.success(serviceSlotService.bulkCreate(request), 201));
	}

	@PostMapping
	public ResponseEntity<ApiResponseFormat> create(@RequestBody RequestCreateServiceSlotDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponseUtil.success(serviceSlotService.create(request), 201));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> update(@PathVariable(name = "id", required = true) Long id,
			@RequestBody RequestUpdateServiceSlotDTO request) {
		return ResponseEntity.ok(ApiResponseUtil.success(serviceSlotService.update(id, request), 200));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> delete(@PathVariable(name = "id", required = true) Long id) {
		serviceSlotService.delete(id);
		return ResponseEntity.ok(ApiResponseUtil.success("Deleted", 200));
	}
}