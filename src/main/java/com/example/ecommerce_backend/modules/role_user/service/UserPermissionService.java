package com.example.ecommerce_backend.modules.role_user.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.role_user.dto.request.AssignPermissionRequest;
import com.example.ecommerce_backend.modules.role_user.dto.response.UserPermissionResponse;
import com.example.ecommerce_backend.modules.role_user.entity.Permission;
import com.example.ecommerce_backend.modules.role_user.entity.UserPermission;
import com.example.ecommerce_backend.modules.role_user.exception.DuplicatePermissionAssignmentException;
import com.example.ecommerce_backend.modules.role_user.exception.PermissionNotFoundException;
import com.example.ecommerce_backend.modules.role_user.exception.UserPermissionNotFoundException;
import com.example.ecommerce_backend.modules.role_user.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.role_user.repository.UserPermissionRepository;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import com.example.ecommerce_backend.modules.role_user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.role_user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class UserPermissionService {

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionsRepository permissionsRepository;

    @Transactional
    @RequiresPermission("user_permission:write")
    public UserPermissionResponse assignPermission(Long userId, AssignPermissionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Permission permission = permissionsRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new PermissionNotFoundException(request.getPermissionId()));

        if (userPermissionRepository.findByUserIdAndPermissionId(userId, request.getPermissionId()).isPresent()) {
            throw new DuplicatePermissionAssignmentException(userId, request.getPermissionId());
        }

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
    public Page<UserPermissionResponse> getUserPermissions(Long userId, Pageable pageable) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return userPermissionRepository.findByUserId(userId, pageable)
                .map(up -> UserPermissionResponse.builder()
                        .id(up.getId())
                        .userId(up.getUser().getId())
                        .permissionName(up.getPermission().getPermissionName())
                        .effect(up.getEffect())
                        .build());
    }

    @Transactional
    @RequiresPermission("user_permission:write")
    public void removePermission(Long userPermissionId) {
        UserPermission userPermission = userPermissionRepository.findById(userPermissionId)
                .orElseThrow(() -> new UserPermissionNotFoundException(userPermissionId));
        userPermissionRepository.delete(userPermission);
    }
}
