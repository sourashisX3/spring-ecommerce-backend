package com.example.ecommerce_backend.modules.permission.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.permission.dto.request.CreatePermissionRequest;
import com.example.ecommerce_backend.modules.permission.dto.response.PermissionResponse;
import com.example.ecommerce_backend.modules.permission.entity.Permission;
import com.example.ecommerce_backend.modules.permission.exception.PermissionAlreadyExistsException;
import com.example.ecommerce_backend.modules.permission.exception.PermissionInUseException;
import com.example.ecommerce_backend.modules.permission.exception.PermissionNotFoundException;
import com.example.ecommerce_backend.modules.permission.mapper.PermissionMapper;
import com.example.ecommerce_backend.modules.permission.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.role.repository.RolesRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.userpermission.entity.UserPermission;
import com.example.ecommerce_backend.modules.userpermission.repository.UserPermissionRepository;
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

    @Transactional(readOnly = true)
    public Set<String> getEffectivePermissions(User user) {
        Set<String> permissions = new HashSet<>();

        if (user.getRole() != null) {
            for (Permission p : user.getRole().getPermissions()) {
                String permName = p.getPermissionName();
                if (permName.equals("*:*")) {
                    // Expand wildcard to all known permissions
                    permissions.addAll(permissionsRepository.findAll().stream()
                            .map(Permission::getPermissionName)
                            .collect(Collectors.toSet()));
                } else if (permName.endsWith(":*")) {
                    // Expand resource:* wildcard
                    String resource = permName.substring(0, permName.length() - 2);
                    permissions.addAll(permissionsRepository.findAll().stream()
                            .map(Permission::getPermissionName)
                            .filter(pn -> pn.startsWith(resource + ":"))
                            .collect(Collectors.toSet()));
                } else if (permName.startsWith("*:") ) {
                    // Expand *:action wildcard
                    String action = permName.substring(2);
                    permissions.addAll(permissionsRepository.findAll().stream()
                            .map(Permission::getPermissionName)
                            .filter(pn -> pn.endsWith(":" + action))
                            .collect(Collectors.toSet()));
                } else {
                    permissions.add(permName);
                }
            }
        }

        List<UserPermission> userPermissions = userPermissionRepository.findByUserId(user.getId());
        for (UserPermission up : userPermissions) {
            String permName = up.getPermission().getPermissionName();
            if (up.getEffect() == UserPermission.Effect.DENY) {
                permissions.remove(permName);
            } else {
                // Also expand wildcards for user-specific permissions
                if (permName.equals("*:*")) {
                    permissions.addAll(permissionsRepository.findAll().stream()
                            .map(Permission::getPermissionName)
                            .collect(Collectors.toSet()));
                } else if (permName.endsWith(":*")) {
                    String resource = permName.substring(0, permName.length() - 2);
                    permissions.addAll(permissionsRepository.findAll().stream()
                            .map(Permission::getPermissionName)
                            .filter(pn -> pn.startsWith(resource + ":"))
                            .collect(Collectors.toSet()));
                } else if (permName.startsWith("*:") ) {
                    String action = permName.substring(2);
                    permissions.addAll(permissionsRepository.findAll().stream()
                            .map(Permission::getPermissionName)
                            .filter(pn -> pn.endsWith(":" + action))
                            .collect(Collectors.toSet()));
                } else {
                    permissions.add(permName);
                }
            }
        }

        return permissions;
    }

    @Transactional(readOnly = true)
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
        return PermissionMapper.toPermissionResponse(permission);
    }

    @Transactional(readOnly = true)
    public Page<PermissionResponse> getAllPermissions(Pageable pageable) {
        return permissionsRepository.findAll(pageable)
                .map(PermissionMapper::toPermissionResponse);
    }

    @Transactional
    @RequiresPermission("permission:write")
    public void deletePermission(Long id) {
        Permission permission = permissionsRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id));

        long roleCount = rolesRepository.countByPermissionId(id);
        if (roleCount > 0) {
            throw new PermissionInUseException(permission.getPermissionName());
        }

        long userPermCount = userPermissionRepository.countByPermissionId(id);
        if (userPermCount > 0) {
            throw new PermissionInUseException(permission.getPermissionName());
        }

        permissionsRepository.delete(permission);
    }
}
