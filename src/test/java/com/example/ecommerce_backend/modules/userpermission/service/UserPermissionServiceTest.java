package com.example.ecommerce_backend.modules.userpermission.service;

import com.example.ecommerce_backend.modules.permission.entity.Permission;
import com.example.ecommerce_backend.modules.permission.exception.PermissionNotFoundException;
import com.example.ecommerce_backend.modules.permission.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import com.example.ecommerce_backend.modules.userpermission.dto.request.AssignPermissionRequest;
import com.example.ecommerce_backend.modules.userpermission.dto.response.UserPermissionResponse;
import com.example.ecommerce_backend.modules.userpermission.entity.UserPermission;
import com.example.ecommerce_backend.modules.userpermission.exception.DuplicatePermissionAssignmentException;
import com.example.ecommerce_backend.modules.userpermission.exception.UserPermissionNotFoundException;
import com.example.ecommerce_backend.modules.userpermission.repository.UserPermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPermissionServiceTest {

    @Mock
    private UserPermissionRepository userPermissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PermissionsRepository permissionsRepository;

    @InjectMocks
    private UserPermissionService userPermissionService;

    private User user;
    private Permission permission;
    private UserPermission userPermission;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("user@test.com").firstName("John").build();
        permission = Permission.builder().id(1L).permissionName("product:read").build();

        userPermission = UserPermission.builder()
                .id(1L).user(user).permission(permission)
                .effect(UserPermission.Effect.GRANT)
                .build();
    }

    // --- assignPermission ---

    @Test
    void assignPermission_shouldSaveAndReturn() {
        AssignPermissionRequest request = new AssignPermissionRequest();
        request.setPermissionId(1L);
        request.setEffect(UserPermission.Effect.GRANT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(permissionsRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(userPermissionRepository.findByUserIdAndPermissionId(1L, 1L)).thenReturn(Optional.empty());
        when(userPermissionRepository.save(any())).thenReturn(userPermission);

        UserPermissionResponse result = userPermissionService.assignPermission(1L, request);

        assertThat(result.getPermissionName()).isEqualTo("product:read");
        assertThat(result.getEffect()).isEqualTo(UserPermission.Effect.GRANT);
    }

    @Test
    void assignPermission_whenUserNotFound_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userPermissionService.assignPermission(99L, new AssignPermissionRequest()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void assignPermission_whenPermissionNotFound_shouldThrow() {
        AssignPermissionRequest request = new AssignPermissionRequest();
        request.setPermissionId(99L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(permissionsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userPermissionService.assignPermission(1L, request))
                .isInstanceOf(PermissionNotFoundException.class);
    }

    @Test
    void assignPermission_whenDuplicate_shouldThrow() {
        AssignPermissionRequest request = new AssignPermissionRequest();
        request.setPermissionId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(permissionsRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(userPermissionRepository.findByUserIdAndPermissionId(1L, 1L)).thenReturn(Optional.of(userPermission));

        assertThatThrownBy(() -> userPermissionService.assignPermission(1L, request))
                .isInstanceOf(DuplicatePermissionAssignmentException.class);
    }

    // --- getUserPermissions ---

    @Test
    void getUserPermissions_shouldReturnPage() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userPermissionRepository.findByUserId(eq(1L), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(userPermission)));

        Page<UserPermissionResponse> result = userPermissionService.getUserPermissions(1L, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPermissionName()).isEqualTo("product:read");
    }

    @Test
    void getUserPermissions_whenUserNotFound_shouldThrow() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userPermissionService.getUserPermissions(99L, PageRequest.of(0, 20)))
                .isInstanceOf(UserNotFoundException.class);
    }

    // --- removePermission ---

    @Test
    void removePermission_shouldDelete() {
        when(userPermissionRepository.findById(1L)).thenReturn(Optional.of(userPermission));

        userPermissionService.removePermission(1L);

        verify(userPermissionRepository).delete(userPermission);
    }

    @Test
    void removePermission_whenNotFound_shouldThrow() {
        when(userPermissionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userPermissionService.removePermission(99L))
                .isInstanceOf(UserPermissionNotFoundException.class);
    }
}
