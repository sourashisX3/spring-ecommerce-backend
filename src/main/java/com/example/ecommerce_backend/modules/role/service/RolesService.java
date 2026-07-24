package com.example.ecommerce_backend.modules.role.service;

import com.example.ecommerce_backend.modules.role.dto.request.RoleRequest;
import com.example.ecommerce_backend.modules.role.dto.response.RolesResponse;
import com.example.ecommerce_backend.modules.role.entity.Permission;
import com.example.ecommerce_backend.modules.role.entity.Role;
import com.example.ecommerce_backend.modules.role.exception.PermissionNotFoundException;
import com.example.ecommerce_backend.modules.role.exception.PermissionRequiredException;
import com.example.ecommerce_backend.modules.role.exception.RoleAlreadyExistsException;
import com.example.ecommerce_backend.modules.role.exception.RoleNotFoundException;
import com.example.ecommerce_backend.modules.role.mapper.RolesMapper;
import com.example.ecommerce_backend.modules.role.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.role.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PermissionsRepository permissionsRepository;

    @Transactional(readOnly = true)
    public List<RolesResponse> getAllRoles() {
        return rolesRepository
                .findAll()
                .stream()
                .map(RolesMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    @Transactional
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
    public void deleteRole(Long id) {
        Role role = rolesRepository
                .findById(id)
                .orElseThrow(RoleNotFoundException::new);
        rolesRepository.delete(role);
    }

}
