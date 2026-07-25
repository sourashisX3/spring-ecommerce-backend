package com.example.ecommerce_backend.modules.role_user.service;

import com.example.ecommerce_backend.modules.role_user.dto.response.RolesResponse;
import com.example.ecommerce_backend.modules.role_user.entity.Role;
import com.example.ecommerce_backend.modules.role_user.repository.RolesRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolesServiceTest {

    @Mock
    private RolesRepository rolesRepository;

    @InjectMocks
    private RolesService rolesService;

    private Role adminRole;

    @BeforeEach
    void setUp() {
        adminRole = Role.builder()
                .id(1L).roleName("ADMIN")
                .roleDescription("Administrator")
                .build();
    }

    @Test
    void getAllRoles_withSearch_shouldDelegateToRepo() {
        when(rolesRepository.findBySearchTerm(eq("admin"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(adminRole)));

        Page<RolesResponse> result = rolesService.getAllRoles("admin", PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRoleName()).isEqualTo("ADMIN");
    }

    @Test
    void getAllRoles_withNullSearch_shouldReturnAll() {
        when(rolesRepository.findBySearchTerm(isNull(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(adminRole)));

        Page<RolesResponse> result = rolesService.getAllRoles(null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAllRoles_withEmptySearch_shouldReturnAll() {
        when(rolesRepository.findBySearchTerm(eq(""), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(adminRole)));

        Page<RolesResponse> result = rolesService.getAllRoles("", PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAllRoles_shouldMapToRolesResponse() {
        when(rolesRepository.findBySearchTerm(isNull(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(adminRole)));

        Page<RolesResponse> result = rolesService.getAllRoles(null, PageRequest.of(0, 20));

        RolesResponse response = result.getContent().get(0);
        assertThat(response.getRoleName()).isEqualTo("ADMIN");
        assertThat(response.getRoleDescription()).isEqualTo("Administrator");
    }
}
