package com.example.ecommerce_backend.modules.user.service;

import com.example.ecommerce_backend.modules.role.entity.Role;
import com.example.ecommerce_backend.modules.user.dto.response.UserResponse;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        Role role = Role.builder().id(1L).roleName("USER").build();

        user1 = User.builder()
                .id(1L).uuid("uuid-1")
                .firstName("John").lastName("Doe")
                .email("john@example.com").dialCode("+1")
                .phoneNumber("1234567890")
                .password("pass").isActive(true)
                .role(role).build();

        user2 = User.builder()
                .id(2L).uuid("uuid-2")
                .firstName("Jane").lastName("Smith")
                .email("jane@example.com").dialCode("+1")
                .phoneNumber("9876543210")
                .password("pass").isActive(false)
                .role(role).build();
    }

    @Test
    void getAllUsers_withSearchAndActive_shouldDelegateToRepo() {
        when(userRepository.findBySearchTerm(eq("john"), eq(true), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(user1)));

        Page<UserResponse> result = userService.getAllUsers("john", true, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void getAllUsers_withNullSearch_shouldReturnAll() {
        when(userRepository.findBySearchTerm(isNull(), isNull(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(user1, user2)));

        Page<UserResponse> result = userService.getAllUsers(null, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void getAllUsers_withInactiveFilter_shouldReturnOnlyInactive() {
        when(userRepository.findBySearchTerm(isNull(), eq(false), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(user2)));

        Page<UserResponse> result = userService.getAllUsers(null, false, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).isActive()).isFalse();
    }

    @Test
    void getAllUsers_shouldMapToUserResponse() {
        when(userRepository.findBySearchTerm(isNull(), isNull(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(user1)));

        Page<UserResponse> result = userService.getAllUsers(null, null, PageRequest.of(0, 20));

        UserResponse response = result.getContent().get(0);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getRoleName()).isEqualTo("USER");
        assertThat(response.isActive()).isTrue();
    }
}
