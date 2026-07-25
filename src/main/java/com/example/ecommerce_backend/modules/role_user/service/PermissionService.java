package com.example.ecommerce_backend.modules.role_user.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.role_user.dto.request.CreatePermissionRequest;
import com.example.ecommerce_backend.modules.role_user.dto.response.PermissionResponse;
import com.example.ecommerce_backend.modules.role_user.entity.Permission;
import com.example.ecommerce_backend.modules.role_user.entity.UserPermission;
import com.example.ecommerce_backend.modules.role_user.exception.PermissionAlreadyExistsException;
import com.example.ecommerce_backend.modules.role_user.exception.PermissionInUseException;
import com.example.ecommerce_backend.modules.role_user.exception.PermissionNotFoundException;
import com.example.ecommerce_backend.modules.role_user.mapper.RolesMapper;
import com.example.ecommerce_backend.modules.role_user.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.role_user.repository.RolesRepository;
import com.example.ecommerce_backend.modules.role_user.repository.UserPermissionRepository;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private PermissionsRepository permissionsRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    public Set<String> getEffectivePermissions(User user) {
        Set<String> permissions = new HashSet<>();

        if (user.getRole() != null) {
            permissions.addAll(user.getRole().getPermissions().stream()
                    .map(Permission::getPermissionName)
                    .collect(Collectors.toSet()));
        }

        List<UserPermission> userPermissions = userPermissionRepository.findByUserId(user.getId());
        for (UserPermission up : userPermissions) {
            String permName = up.getPermission().getPermissionName();
            if (up.getEffect() == UserPermission.Effect.DENY) {
                permissions.remove(permName);
            } else {
                permissions.add(permName);
            }
        }

        return permissions;
    }

    public boolean hasPermission(User user, String requiredPermission) {
        if (user.getRole() == null) {
            return false;
        }

        List<UserPermission> userPermissions = userPermissionRepository.findByUserId(user.getId());

        for (UserPermission up : userPermissions) {
            if (matches(up.getPermission().getPermissionName(), requiredPermission)) {
                if (up.getEffect() == UserPermission.Effect.DENY) {
                    return false;
                }
            }
        }

        for (UserPermission up : userPermissions) {
            if (matches(up.getPermission().getPermissionName(), requiredPermission)) {
                if (up.getEffect() == UserPermission.Effect.GRANT) {
                    return true;
                }
            }
        }

        return user.getRole().getPermissions().stream()
                .anyMatch(p -> matches(p.getPermissionName(), requiredPermission));
    }

    private boolean matches(String permissionName, String required) {
        if (permissionName.equals(required)) return true;
        if (permissionName.equals("*:*")) return true;

        String[] parts = permissionName.split(":", 2);
        if (parts.length < 2) return false;
        if (parts[1].equals("*") && required.startsWith(parts[0] + ":")) return true;
        return parts[0].equals("*") && required.endsWith(":" + parts[1]);
    }

    @Transactional
    @RequiresPermission("permission:write")
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        if (permissionsRepository.findByPermissionName(request.getPermissionName()).isPresent()) {
            throw new PermissionAlreadyExistsException(request.getPermissionName());
        }

        Permission permission = Permission.builder()
                .permissionName(request.getPermissionName())
                .permissionDescription(request.getPermissionDescription())
                .build();

        permission = permissionsRepository.save(permission);
        return RolesMapper.toPermissionResponse(permission);
    }

    @Transactional(readOnly = true)
    public Page<PermissionResponse> getAllPermissions(Pageable pageable) {
        return permissionsRepository.findAll(pageable)
                .map(RolesMapper::toPermissionResponse);
    }

    @Transactional
    @RequiresPermission("permission:write")
    public void deletePermission(Long id) {
        Permission permission = permissionsRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id));

        boolean inRoles = rolesRepository.findAll().stream()
                .anyMatch(role -> role.getPermissions().stream().anyMatch(p -> p.getId().equals(id)));

        if (inRoles) {
            throw new PermissionInUseException(permission.getPermissionName());
        }

        boolean inUserPerms = userPermissionRepository.findAll().stream()
                .anyMatch(up -> up.getPermission().getId().equals(id));

        if (inUserPerms) {
            throw new PermissionInUseException(permission.getPermissionName());
        }

        permissionsRepository.delete(permission);
    }
}
