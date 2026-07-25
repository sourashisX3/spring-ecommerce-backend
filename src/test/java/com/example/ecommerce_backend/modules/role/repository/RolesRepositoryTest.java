package com.example.ecommerce_backend.modules.role.repository;

import com.example.ecommerce_backend.modules.role.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RolesRepositoryTest {

    @Autowired
    private RolesRepository rolesRepository;

    @BeforeEach
    void setUp() {
        rolesRepository.save(Role.builder()
                .roleName("ADMIN").roleDescription("Administrator role").build());
        rolesRepository.save(Role.builder()
                .roleName("MANAGER").roleDescription("Manages products").build());
    }

    @Test
    void findBySearchTerm_shouldMatchRoleName() {
        Page<Role> result = rolesRepository.findBySearchTerm("admin", PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRoleName()).isEqualTo("ADMIN");
    }

    @Test
    void findBySearchTerm_shouldMatchRoleDescription() {
        Page<Role> result = rolesRepository.findBySearchTerm("manages", PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findBySearchTerm_shouldBeCaseInsensitive() {
        Page<Role> result = rolesRepository.findBySearchTerm("Admin", PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findBySearchTerm_shouldReturnAllWhenSearchIsNull() {
        Page<Role> result = rolesRepository.findBySearchTerm(null, PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void findBySearchTerm_shouldReturnAllWhenSearchIsEmpty() {
        Page<Role> result = rolesRepository.findBySearchTerm("", PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void findBySearchTerm_shouldReturnEmptyForNoMatch() {
        Page<Role> result = rolesRepository.findBySearchTerm("nonexistent", PageRequest.of(0, 20));
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByRoleName_shouldReturnRole() {
        assertThat(rolesRepository.findByRoleName("ADMIN")).isPresent();
    }

    @Test
    void findByRoleName_shouldReturnEmptyForNonExistent() {
        assertThat(rolesRepository.findByRoleName("NONEXISTENT")).isEmpty();
    }

    @Test
    void existsByRoleName_shouldReturnTrueForExisting() {
        assertThat(rolesRepository.existsByRoleName("ADMIN")).isTrue();
    }
}
