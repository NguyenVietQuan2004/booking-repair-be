package com.hange.booking.auth.controller.permission;

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

import com.hange.booking.auth.dto.permission.PermissionFilterDTO;
import com.hange.booking.auth.entity.permission.Permission;
import com.hange.booking.auth.service.permission.PermissionService;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseFormat;
import com.hange.booking.auth.utils.FormatResponse.ApiResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

	private final PermissionService permissionService;

	@PostMapping
	public ResponseEntity<ApiResponseFormat> create(@RequestBody Permission permission) {
		return ResponseEntity.ok(ApiResponseUtil.success(permissionService.create(permission), HttpStatus.OK.value()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> update(@PathVariable(name = "id", required = true) Long id,
			@RequestBody Permission permission) {
		return ResponseEntity
				.ok(ApiResponseUtil.success(permissionService.update(id, permission), HttpStatus.OK.value()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> getById(@PathVariable(name = "id", required = true) Long id) {
		return ResponseEntity.ok(ApiResponseUtil.success(permissionService.getById(id), HttpStatus.OK.value()));
	}

	@GetMapping
	public ResponseEntity<ApiResponseFormat> getAll(PermissionFilterDTO filter,
			@PageableDefault(size = 999) Pageable pageable) {
		Page<Permission> permissions = permissionService.getAll(filter, pageable);
		return ResponseEntity.ok(ApiResponseUtil.success(permissions.getContent(), HttpStatus.OK.value()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseFormat> delete(@PathVariable(name = "id", required = true) Long id) {
		permissionService.delete(id);
		return ResponseEntity.ok(ApiResponseUtil.success("Delete permission success", HttpStatus.OK.value()));
	}

}