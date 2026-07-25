package com.example.ecommerce_backend.modules.role.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.permission.entity.Permission;
import com.example.ecommerce_backend.modules.permission.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.role.dto.request.RoleRequest;
import com.example.ecommerce_backend.modules.role.dto.response.RolesResponse;
import com.example.ecommerce_backend.modules.role.entity.Role;
import com.example.ecommerce_backend.modules.role.exception.RoleAlreadyExistsException;
import com.example.ecommerce_backend.modules.role.exception.RoleNotFoundException;
import com.example.ecommerce_backend.modules.role.mapper.RolesMapper;
import com.example.ecommerce_backend.modules.role.repository.RolesRepository;
import com.example.ecommerce_backend.modules.user.exception.CannotDeleteProtectedRoleException;
import com.example.ecommerce_backend.modules.permission.exception.PermissionNotFoundException;
import com.example.ecommerce_backend.modules.permission.exception.PermissionRequiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RolesService {

    private static final Set<String> PROTECTED_ROLES = Set.of("SUPER_ADMIN");

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PermissionsRepository permissionsRepository;

    @Transactional(readOnly = true)
    public Page<RolesResponse> getAllRoles(String search, Pageable pageable) {
        return rolesRepository.findBySearchTerm(search, pageable)
                .map(RolesMapper::toRoleResponse);
    }

    @Transactional
    @RequiresPermission("role:write")
    public RolesResponse createRole(RoleRequest request) {

        String roleName = request.getRoleName();
        String roleDescription = request.getRoleDescription();

        if (rolesRepository.findByRoleName(roleName).isPresent()) {
            throw new RoleAlreadyExistsException(roleName);
        }

        Set<Permission> permissions = new HashSet<>();
        Set<Long> permissionIds = request.getRolePermissionIds();

        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new PermissionRequiredException();
        }

        List<Permission> found = permissionsRepository.findAllById(permissionIds);
        for (Long id : permissionIds) {
            Permission permission = found.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new PermissionNotFoundException(id));
            permissions.add(permission);
        }

        Role role = Role
                .builder()
                .roleName(roleName)
                .roleDescription(roleDescription)
                .permissions(permissions)
                .build();
        role = rolesRepository.save(role);
        return RolesMapper.toRoleResponse(role);
    }

    @Transactional
    @RequiresPermission("role:write")
    public void deleteRole(Long id) {
        Role role = rolesRepository
                .findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));

        if (PROTECTED_ROLES.contains(role.getRoleName())) {
            throw new CannotDeleteProtectedRoleException(role.getRoleName());
        }

        rolesRepository.delete(role);
    }

}
