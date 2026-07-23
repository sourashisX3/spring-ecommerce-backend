package com.example.ecommerce_backend.modules.role.service;

import com.example.ecommerce_backend.core.exception.BaseException;
import com.example.ecommerce_backend.modules.role.dto.request.CreatePermissionRequest;
import com.example.ecommerce_backend.modules.role.dto.response.PermissionResponse;
import com.example.ecommerce_backend.modules.role.entity.Permission;
import com.example.ecommerce_backend.modules.role.entity.UserPermission;
import com.example.ecommerce_backend.modules.role.mapper.RolesMapper;
import com.example.ecommerce_backend.modules.role.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.role.repository.UserPermissionRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private PermissionsRepository permissionsRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

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
        if (parts.length == 2 && parts[1].equals("*")) {
            return required.startsWith(parts[0] + ":");
        }
        return false;
    }

    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        if (permissionsRepository.findByPermissionName(request.getPermissionName()).isPresent()) {
            throw new BaseException("Permission '" + request.getPermissionName() + "' already exists", HttpStatus.CONFLICT);
        }

        Permission permission = Permission.builder()
                .permissionName(request.getPermissionName())
                .permissionDescription(request.getPermissionDescription())
                .build();

        permission = permissionsRepository.save(permission);
        return RolesMapper.toPermissionResponse(permission);
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionsRepository.findAll()
                .stream()
                .map(RolesMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionsRepository.findById(id)
                .orElseThrow(() -> new BaseException("Permission not found", HttpStatus.NOT_FOUND));
        permissionsRepository.delete(permission);
    }
}
