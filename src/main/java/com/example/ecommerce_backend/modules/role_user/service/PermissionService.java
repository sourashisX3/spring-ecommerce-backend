package com.example.ecommerce_backend.modules.role_user.service;

import com.example.ecommerce_backend.modules.role_user.dto.request.CreatePermissionRequest;
import com.example.ecommerce_backend.modules.role_user.dto.response.PermissionResponse;
import com.example.ecommerce_backend.modules.role_user.entity.Permission;
import com.example.ecommerce_backend.modules.role_user.entity.UserPermission;
import com.example.ecommerce_backend.modules.role_user.exception.PermissionAlreadyExistsException;
import com.example.ecommerce_backend.modules.role_user.exception.PermissionNotFoundException;
import com.example.ecommerce_backend.modules.role_user.mapper.RolesMapper;
import com.example.ecommerce_backend.modules.role_user.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.role_user.repository.UserPermissionRepository;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<PermissionResponse> getAllPermissions() {
        return permissionsRepository.findAll()
                .stream()
                .map(RolesMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionsRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id));
//                .orElseThrow(() -> new PermissionNotFoundException());
        permissionsRepository.delete(permission);
    }
}
