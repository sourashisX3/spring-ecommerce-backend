package com.example.ecommerce_backend.modules.permission.service;

import com.example.ecommerce_backend.modules.permission.dto.request.CreatePermissionRequest;
import com.example.ecommerce_backend.modules.permission.dto.response.PermissionResponse;
import com.example.ecommerce_backend.modules.permission.entity.Permission;
import com.example.ecommerce_backend.modules.permission.exception.PermissionAlreadyExistsException;
import com.example.ecommerce_backend.modules.permission.exception.PermissionInUseException;
import com.example.ecommerce_backend.modules.permission.exception.PermissionNotFoundException;
import com.example.ecommerce_backend.modules.permission.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.role.entity.Role;
import com.example.ecommerce_backend.modules.role.repository.RolesRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.userpermission.entity.UserPermission;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionsRepository permissionsRepository;

    @Mock
    private RolesRepository rolesRepository;

    @Mock
    private UserPermissionRepository userPermissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    private Permission productRead;
    private Permission productWrite;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        productRead = Permission.builder()
                .id(1L).permissionName("product:read").permissionDescription("Read products").build();

        productWrite = Permission.builder()
                .id(2L).permissionName("product:write").permissionDescription("Write products").build();

        role = Role.builder().id(1L).roleName("ADMIN").permissions(new HashSet<>(Set.of(productRead))).build();

        user = User.builder().id(1L).email("admin@test.com").role(role).build();
    }

    // --- getEffectivePermissions ---

    @Test
    void getEffectivePermissions_shouldReturnRolePermissions() {
        when(userPermissionRepository.findByUserId(1L)).thenReturn(List.of());

        Set<String> result = permissionService.getEffectivePermissions(user);

        assertThat(result).containsExactly("product:read");
    }

    @Test
    void getEffectivePermissions_shouldIncludeGrantedUserPermissions() {
        Permission extra = Permission.builder().id(3L).permissionName("user:read").build();
        UserPermission up = UserPermission.builder()
                .permission(extra).effect(UserPermission.Effect.GRANT).build();

        when(userPermissionRepository.findByUserId(1L)).thenReturn(List.of(up));

        Set<String> result = permissionService.getEffectivePermissions(user);

        assertThat(result).contains("product:read", "user:read");
    }

    @Test
    void getEffectivePermissions_shouldRemoveDeniedPermissions() {
        Permission denied = Permission.builder().id(3L).permissionName("product:read").build();
        UserPermission up = UserPermission.builder()
                .permission(denied).effect(UserPermission.Effect.DENY).build();

        when(userPermissionRepository.findByUserId(1L)).thenReturn(List.of(up));

        Set<String> result = permissionService.getEffectivePermissions(user);

        assertThat(result).doesNotContain("product:read");
    }

    @Test
    void getEffectivePermissions_whenUserHasNoRole_shouldReturnEmpty() {
        user.setRole(null);
        when(userPermissionRepository.findByUserId(1L)).thenReturn(List.of());

        Set<String> result = permissionService.getEffectivePermissions(user);

        assertThat(result).isEmpty();
    }

    // --- hasPermission ---

    @Test
    void hasPermission_exactMatch_shouldReturnTrue() {
        when(userPermissionRepository.findByUserId(1L)).thenReturn(List.of());

        boolean result = permissionService.hasPermission(user, "product:read");

        assertThat(result).isTrue();
    }

    @Test
    void hasPermission_wildcardAll_shouldReturnTrue() {
        Permission allPerm = Permission.builder().id(3L).permissionName("*:*").build();
        role.getPermissions().add(allPerm);
        when(userPermissionRepository.findByUserId(1L)).thenReturn(List.of());

        boolean result = permissionService.hasPermission(user, "anything:read");

        assertThat(result).isTrue();
    }

    @Test
    void hasPermission_resourceWildcard_shouldReturnTrue() {
        Permission wildPerm = Permission.builder().id(3L).permissionName("product:*").build();
        role.getPermissions().add(wildPerm);
        when(userPermissionRepository.findByUserId(1L)).thenReturn(List.of());

        boolean result = permissionService.hasPermission(user, "product:delete");

        assertThat(result).isTrue();
    }

    @Test
    void hasPermission_actionWildcard_shouldReturnTrue() {
        Permission wildPerm = Permission.builder().id(3L).permissionName("*:read").build();
        role.getPermissions().add(wildPerm);
        when(userPermissionRepository.findByUserId(1L)).thenReturn(List.of());

        boolean result = permissionService.hasPermission(user, "user:read");

        assertThat(result).isTrue();
    }

    @Test
    void hasPermission_deniedOverride_shouldReturnFalse() {
        Permission denied = Permission.builder().id(3L).permissionName("product:read").build();
        UserPermission up = UserPermission.builder()
                .permission(denied).effect(UserPermission.Effect.DENY).build();

        when(userPermissionRepository.findByUserId(1L)).thenReturn(List.of(up));

        boolean result = permissionService.hasPermission(user, "product:read");

        assertThat(result).isFalse();
    }

    @Test
    void hasPermission_noMatch_shouldReturnFalse() {
        when(userPermissionRepository.findByUserId(1L)).thenReturn(List.of());

        boolean result = permissionService.hasPermission(user, "user:write");

        assertThat(result).isFalse();
    }

    @Test
    void hasPermission_whenUserHasNoRole_shouldReturnFalse() {
        user.setRole(null);

        boolean result = permissionService.hasPermission(user, "product:read");

        assertThat(result).isFalse();
    }

    // --- createPermission ---

    @Test
    void createPermission_shouldSaveAndReturn() {
        CreatePermissionRequest request = new CreatePermissionRequest();
        request.setPermissionName("user:write");
        request.setPermissionDescription("Write users");

        when(permissionsRepository.findByPermissionName("user:write")).thenReturn(Optional.empty());
        when(permissionsRepository.save(any())).thenAnswer(invocation -> {
            Permission p = invocation.getArgument(0);
            p.setId(3L);
            return p;
        });

        PermissionResponse result = permissionService.createPermission(request);

        assertThat(result.getPermissionName()).isEqualTo("user:write");
        verify(permissionsRepository).save(any());
    }

    @Test
    void createPermission_whenAlreadyExists_shouldThrow() {
        CreatePermissionRequest request = new CreatePermissionRequest();
        request.setPermissionName("product:read");

        when(permissionsRepository.findByPermissionName("product:read")).thenReturn(Optional.of(productRead));

        assertThatThrownBy(() -> permissionService.createPermission(request))
                .isInstanceOf(PermissionAlreadyExistsException.class);
    }

    // --- getAllPermissions ---

    @Test
    void getAllPermissions_shouldReturnPage() {
        when(permissionsRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(productRead, productWrite)));

        Page<PermissionResponse> result = permissionService.getAllPermissions(PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getPermissionName()).isEqualTo("product:read");
    }

    // --- deletePermission ---

    @Test
    void deletePermission_shouldDeleteWhenNotInUse() {
        when(permissionsRepository.findById(1L)).thenReturn(Optional.of(productRead));
        when(rolesRepository.findAll()).thenReturn(List.of());
        when(userPermissionRepository.findAll()).thenReturn(List.of());

        permissionService.deletePermission(1L);

        verify(permissionsRepository).delete(productRead);
    }

    @Test
    void deletePermission_whenNotFound_shouldThrow() {
        when(permissionsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> permissionService.deletePermission(99L))
                .isInstanceOf(PermissionNotFoundException.class);
    }

    @Test
    void deletePermission_whenAssignedToRole_shouldThrow() {
        Role roleWithPerm = Role.builder().id(1L).permissions(Set.of(productRead)).build();

        when(permissionsRepository.findById(1L)).thenReturn(Optional.of(productRead));
        when(rolesRepository.findAll()).thenReturn(List.of(roleWithPerm));

        assertThatThrownBy(() -> permissionService.deletePermission(1L))
                .isInstanceOf(PermissionInUseException.class);
    }

    @Test
    void deletePermission_whenAssignedToUser_shouldThrow() {
        UserPermission up = UserPermission.builder()
                .permission(productRead).build();

        when(permissionsRepository.findById(1L)).thenReturn(Optional.of(productRead));
        when(rolesRepository.findAll()).thenReturn(List.of());
        when(userPermissionRepository.findAll()).thenReturn(List.of(up));

        assertThatThrownBy(() -> permissionService.deletePermission(1L))
                .isInstanceOf(PermissionInUseException.class);
    }
}
