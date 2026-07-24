package com.example.ecommerce_backend.modules.role.service;

import com.example.ecommerce_backend.modules.role.dto.request.AssignPermissionRequest;
import com.example.ecommerce_backend.modules.role.dto.response.UserPermissionResponse;
import com.example.ecommerce_backend.modules.role.entity.Permission;
import com.example.ecommerce_backend.modules.role.entity.UserPermission;
import com.example.ecommerce_backend.modules.role.exception.PermissionNotFoundException;
import com.example.ecommerce_backend.modules.role.exception.UserPermissionNotFoundException;
import com.example.ecommerce_backend.modules.role.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.role.repository.UserPermissionRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserPermissionService {

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionsRepository permissionsRepository;

    @Transactional
    public UserPermissionResponse assignPermission(Long userId, AssignPermissionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Permission permission = permissionsRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new PermissionNotFoundException(request.getPermissionId()));

        UserPermission userPermission = UserPermission.builder()
                .user(user)
                .permission(permission)
                .effect(request.getEffect())
                .build();

        userPermission = userPermissionRepository.save(userPermission);

        return UserPermissionResponse.builder()
                .id(userPermission.getId())
                .userId(user.getId())
                .permissionName(permission.getPermissionName())
                .effect(userPermission.getEffect())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserPermissionResponse> getUserPermissions(Long userId) {

        if (!userPermissionRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return userPermissionRepository.findByUserId(userId)
                .stream()
                .map(up -> UserPermissionResponse.builder()
                        .id(up.getId())
                        .userId(up.getUser().getId())
                        .permissionName(up.getPermission().getPermissionName())
                        .effect(up.getEffect())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void removePermission(Long userPermissionId) {
        UserPermission userPermission = userPermissionRepository.findById(userPermissionId)
                .orElseThrow(() -> new UserPermissionNotFoundException(userPermissionId));
        userPermissionRepository.delete(userPermission);
    }
}
