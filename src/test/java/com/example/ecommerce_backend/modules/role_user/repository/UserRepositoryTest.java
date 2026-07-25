package com.example.ecommerce_backend.modules.role_user.repository;

import com.example.ecommerce_backend.modules.role_user.entity.Role;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    private Role defaultRole;
    private User activeUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        defaultRole = rolesRepository.save(Role.builder()
                .roleName("USER").build());

        activeUser = userRepository.save(User.builder()
                .firstName("John").lastName("Doe")
                .email("john@example.com").dialCode("+1")
                .phoneNumber("1234567890")
                .password("pass").isActive(true)
                .role(defaultRole).build());

        inactiveUser = userRepository.save(User.builder()
                .firstName("Jane").lastName("Smith")
                .email("jane@example.com").dialCode("+1")
                .phoneNumber("9876543210")
                .password("pass").isActive(false)
                .role(defaultRole).build());
    }

    @Test
    void findBySearchTerm_shouldMatchFirstName() {
        Page<User> result = userRepository.findBySearchTerm("john", null, PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findBySearchTerm_shouldMatchLastName() {
        Page<User> result = userRepository.findBySearchTerm("smith", null, PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findBySearchTerm_shouldMatchEmail() {
        Page<User> result = userRepository.findBySearchTerm("jane@example", null, PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findBySearchTerm_shouldMatchPhoneNumber() {
        Page<User> result = userRepository.findBySearchTerm("1234567890", null, PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findBySearchTerm_shouldBeCaseInsensitive() {
        Page<User> result = userRepository.findBySearchTerm("JOHN", null, PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findBySearchTerm_shouldFilterByActive() {
        Page<User> result = userRepository.findBySearchTerm(null, true, PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).isActive()).isTrue();
    }

    @Test
    void findBySearchTerm_shouldFilterByInactive() {
        Page<User> result = userRepository.findBySearchTerm(null, false, PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).isActive()).isFalse();
    }

    @Test
    void findBySearchTerm_shouldReturnAllWhenSearchIsNullAndActiveIsNull() {
        Page<User> result = userRepository.findBySearchTerm(null, null, PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void findBySearchTerm_shouldReturnAllWhenSearchIsEmpty() {
        Page<User> result = userRepository.findBySearchTerm("", null, PageRequest.of(0, 20));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void findBySearchTerm_shouldReturnEmptyForNoMatch() {
        Page<User> result = userRepository.findBySearchTerm("nonexistent", null, PageRequest.of(0, 20));
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByEmail_shouldReturnUser() {
        Optional<User> found = userRepository.findByEmail("john@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void findByPhoneNumber_shouldReturnUser() {
        Optional<User> found = userRepository.findByPhoneNumber("1234567890");
        assertThat(found).isPresent();
    }

    @Test
    void findByUuid_shouldReturnUser() {
        Optional<User> found = userRepository.findByUuid(activeUser.getUuid());
        assertThat(found).isPresent();
    }
}
