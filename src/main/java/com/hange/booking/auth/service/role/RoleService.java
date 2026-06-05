package com.hange.booking.auth.service.role;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hange.booking.auth.dto.role.RoleCreateRequest;
import com.hange.booking.auth.entity.permission.Permission;
import com.hange.booking.auth.entity.role.Role;
import com.hange.booking.auth.repository.PermissionRepository;
import com.hange.booking.auth.repository.RoleRepository;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorAuthCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;

	public Role create(RoleCreateRequest request) {

		String roleName = request.getName();

		if (roleRepository.existsByName(roleName)) {
			throw new AppRuntimeException(ErrorAuthCode.ROLE_ALREADY_EXISTS);
		}
		List<Long> ids = request.getPermissionIds();

		List<Permission> permissions = new ArrayList<Permission>();

		if (ids != null && !ids.isEmpty()) {
			permissions = permissionRepository.findAllById(ids);
		}
		// CHECK missing permission
		if (permissions.size() != request.getPermissionIds().size()) {
			throw new AppRuntimeException(ErrorAuthCode.PERMISSION_NOT_FOUND);
		}

		Role role = Role.builder().name(roleName).description(request.getDescription()).permissions(permissions)
				.build();

		return roleRepository.save(role);
	}

	public Role update(Long id, Role request) {

		Role role = getById(id);

		role.setDescription(request.getDescription());
		role.setPermissions(request.getPermissions());

		return roleRepository.save(role);
	}

	public Role getById(Long id) {

		return roleRepository.findById(id).orElseThrow(() -> new AppRuntimeException(ErrorAuthCode.ROLE_NOT_FOUND));
	}

	public Role getRole(String roleUserEnum) {

		return roleRepository.findByName(roleUserEnum)
				.orElseThrow(() -> new AppRuntimeException(ErrorAuthCode.ROLE_NOT_FOUND));
	}

	public List<Role> getAll() {
		return roleRepository.findAll();
	}

	public void delete(Long id) {

		Role role = getById(id);

		roleRepository.delete(role);
	}

	public Role addPermission(Role role, Permission permission) {

		if (role.getPermissions().contains(permission)) {
			throw new AppRuntimeException(ErrorAuthCode.PERMISSION_ALREADY_ASSIGNED);
		}

		role.getPermissions().add(permission);

		return roleRepository.save(role);
	}

	public Role removePermission(Role role, Permission permission) {

		if (!role.getPermissions().contains(permission)) {
			throw new AppRuntimeException(ErrorAuthCode.PERMISSION_NOT_ASSIGNED);
		}

		role.getPermissions().remove(permission);

		return roleRepository.save(role);
	}

	public Role updateRole(Long id, RoleCreateRequest request) {

		Role role = roleRepository.findById(id).orElseThrow(() -> new AppRuntimeException(ErrorAuthCode.ROLE_NOT_FOUND));

		// update description
		role.setDescription(request.getDescription());

		// validate role name (optional but recommended)
		String roleName = request.getName();

		role.setName(roleName);

		// handle permissions safely
		List<Long> ids = request.getPermissionIds();

		List<Permission> permissions = new ArrayList<>();

		if (ids != null && !ids.isEmpty()) {
			permissions = permissionRepository.findAllById(ids);

			// check missing permission
			if (permissions.size() != ids.size()) {
				throw new AppRuntimeException(ErrorAuthCode.PERMISSION_NOT_FOUND);
			}
		}

		role.getPermissions().clear();
		role.getPermissions().addAll(permissions);

		return roleRepository.save(role);
	}

}