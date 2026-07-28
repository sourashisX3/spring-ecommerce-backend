package com.example.ecommerce_backend.modules.auth.service;

import com.example.ecommerce_backend.core.auth.JwtTokenProvider;
import com.example.ecommerce_backend.core.event.UserRegisteredEvent;
import com.example.ecommerce_backend.core.service.RefreshTokenService;
import com.example.ecommerce_backend.modules.auth.dto.request.LoginRequest;
import com.example.ecommerce_backend.modules.auth.dto.request.RefreshTokenRequest;
import com.example.ecommerce_backend.modules.auth.dto.request.RegisterRequest;
import com.example.ecommerce_backend.modules.auth.dto.response.AuthResponse;
import com.example.ecommerce_backend.modules.auth.exception.AccountDeactivatedException;
import com.example.ecommerce_backend.modules.auth.exception.InvalidTokenException;
import com.example.ecommerce_backend.modules.otp.dto.request.SendOtpRequest;
import com.example.ecommerce_backend.modules.otp.dto.request.VerifyOtpRequest;
import com.example.ecommerce_backend.modules.otp.exception.InvalidOtpException;
import com.example.ecommerce_backend.modules.otp.service.OtpService;
import com.example.ecommerce_backend.modules.role.entity.Role;
import com.example.ecommerce_backend.modules.role.exception.RoleNotFoundException;
import com.example.ecommerce_backend.modules.role.repository.RolesRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.entity.UserAddress;
import com.example.ecommerce_backend.modules.user.exception.EmailAlreadyExistsException;
import com.example.ecommerce_backend.modules.user.exception.PhoneAlreadyExistsException;
import com.example.ecommerce_backend.modules.user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolesRepository rolesRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private OtpService otpService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    private Role userRole;
    private User activeUser;
    private User inactiveUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userRole = Role.builder().id(1L).roleName("USER").build();

        activeUser = User.builder()
                .id(1L).uuid("uuid-1")
                .firstName("John").lastName("Doe")
                .email("john@test.com").dialCode("+1")
                .phoneNumber("1234567890")
                .password("encoded-pass").isActive(true)
                .isEmailVerified(false).isPhoneVerified(false)
                .role(userRole)
                .address(new UserAddress())
                .build();

        inactiveUser = User.builder()
                .id(2L).uuid("uuid-2")
                .firstName("Jane").lastName("Smith")
                .email("jane@test.com").dialCode("+1")
                .phoneNumber("9876543210")
                .password("encoded-pass").isActive(false)
                .isEmailVerified(false).isPhoneVerified(false)
                .role(userRole)
                .address(new UserAddress())
                .build();

        userDetails = activeUser;
    }

    private void mockBuildAuthResponse() {
        when(userDetailsService.loadUserByUsername(activeUser.getEmail())).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(userDetails)).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn("refresh-token");
        when(refreshTokenService.storeRefreshToken("refresh-token", activeUser.getId())).thenReturn("refresh-token");
    }

    private RegisterRequest createRegisterRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setEmail("john@test.com");
        req.setDialCode("+1");
        req.setPhoneNumber("1234567890");
        req.setPassword("password123");
        return req;
    }

    @Test
    void register_withDefaultRole_shouldSucceed() {
        RegisterRequest request = createRegisterRequest();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(request.getPhoneNumber())).thenReturn(Optional.empty());
        when(rolesRepository.findByRoleName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenReturn(activeUser);
        mockBuildAuthResponse();

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(86400);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getFirstName()).isEqualTo("John");
        assertThat(savedUser.getRole()).isEqualTo(userRole);
        assertThat(savedUser.isActive()).isTrue();

        verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));
    }

    @Test
    void register_withDefaultRoleCreated_shouldCreateRoleIfNotFound() {
        RegisterRequest request = createRegisterRequest();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(request.getPhoneNumber())).thenReturn(Optional.empty());
        when(rolesRepository.findByRoleName("USER")).thenReturn(Optional.empty());
        when(rolesRepository.save(any(Role.class))).thenReturn(userRole);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenReturn(activeUser);
        mockBuildAuthResponse();

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("access-token");
        verify(rolesRepository).save(any(Role.class));
    }

    @Test
    void register_withCustomRole_shouldUseProvidedRole() {
        Role customRole = Role.builder().id(2L).roleName("ADMIN").build();
        RegisterRequest request = createRegisterRequest();
        request.setRoleId(2L);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(request.getPhoneNumber())).thenReturn(Optional.empty());
        when(rolesRepository.findById(2L)).thenReturn(Optional.of(customRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenReturn(activeUser);
        mockBuildAuthResponse();

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRole()).isEqualTo(customRole);
    }

    @Test
    void register_whenCustomRoleNotFound_shouldThrow() {
        RegisterRequest request = createRegisterRequest();
        request.setRoleId(99L);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(request.getPhoneNumber())).thenReturn(Optional.empty());
        when(rolesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    void register_whenEmailAlreadyExists_shouldThrow() {
        RegisterRequest request = createRegisterRequest();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(activeUser));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_whenPhoneAlreadyExists_shouldThrow() {
        RegisterRequest request = createRegisterRequest();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(request.getPhoneNumber())).thenReturn(Optional.of(activeUser));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(PhoneAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_withValidCredentials_shouldSucceed() {
        LoginRequest request = new LoginRequest();
        request.setEmailOrPhone("john@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(activeUser));
        mockBuildAuthResponse();

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("access-token");
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("john@test.com", "password123"));
    }

    @Test
    void login_withPhoneIdentifier_shouldLookupByPhone() {
        LoginRequest request = new LoginRequest();
        request.setEmailOrPhone("1234567890");
        request.setPassword("password123");

        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(activeUser));
        mockBuildAuthResponse();

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("access-token");
        verify(userRepository).findByPhoneNumber("1234567890");
    }

    @Test
    void login_whenUserDeactivated_shouldThrow() {
        LoginRequest request = new LoginRequest();
        request.setEmailOrPhone("jane@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("jane@test.com")).thenReturn(Optional.of(inactiveUser));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AccountDeactivatedException.class);
    }

    @Test
    void login_whenUserNotFound_shouldThrow() {
        LoginRequest request = new LoginRequest();
        request.setEmailOrPhone("nonexistent@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void refresh_withValidToken_shouldReturnNewTokens() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("old-refresh-token");

        when(jwtTokenProvider.validateToken("old-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.extractEmail("old-refresh-token")).thenReturn("john@test.com");
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(activeUser));
        when(refreshTokenService.validateAndRotate("old-refresh-token")).thenReturn("new-refresh-token");
        when(userDetailsService.loadUserByUsername("john@test.com")).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(userDetails)).thenReturn("new-access-token");

        AuthResponse response = authService.refresh(request);

        assertThat(response.getToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void refresh_whenTokenInvalid_shouldThrow() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid-token");

        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void refresh_whenUserNotFound_shouldThrow() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("old-refresh-token");

        when(jwtTokenProvider.validateToken("old-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.extractEmail("old-refresh-token")).thenReturn("unknown@test.com");
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void refresh_whenUserDeactivated_shouldThrow() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("old-refresh-token");

        when(jwtTokenProvider.validateToken("old-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.extractEmail("old-refresh-token")).thenReturn("jane@test.com");
        when(userRepository.findByEmail("jane@test.com")).thenReturn(Optional.of(inactiveUser));

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(AccountDeactivatedException.class);
    }

    @Test
    void sendOtp_withEmail_shouldGenerateOtp() {
        SendOtpRequest request = new SendOtpRequest();
        request.setEmailOrPhone("john@test.com");

        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(activeUser));
        when(otpService.generateOtp("john@test.com")).thenReturn("654321");

        String otp = authService.sendOtp(request);

        assertThat(otp).isEqualTo("654321");
    }

    @Test
    void sendOtp_withPhone_shouldGenerateOtp() {
        SendOtpRequest request = new SendOtpRequest();
        request.setEmailOrPhone("1234567890");

        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(activeUser));
        when(otpService.generateOtp("1234567890")).thenReturn("654321");

        String otp = authService.sendOtp(request);

        assertThat(otp).isEqualTo("654321");
    }

    @Test
    void sendOtp_whenUserNotFound_shouldThrow() {
        SendOtpRequest request = new SendOtpRequest();
        request.setEmailOrPhone("unknown@test.com");

        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.sendOtp(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void verifyOtp_withEmail_shouldSetEmailVerified() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmailOrPhone("john@test.com");
        request.setOtp("123456");

        when(otpService.validateOtp("john@test.com", "123456")).thenReturn(true);
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(activeUser));
        when(userRepository.save(any(User.class))).thenReturn(activeUser);
        mockBuildAuthResponse();

        AuthResponse response = authService.verifyOtp(request);

        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(activeUser.isEmailVerified()).isTrue();
        verify(otpService).invalidateOtp("john@test.com");
    }

    @Test
    void verifyOtp_withPhone_shouldSetPhoneVerified() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmailOrPhone("1234567890");
        request.setOtp("123456");

        when(otpService.validateOtp("1234567890", "123456")).thenReturn(true);
        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(activeUser));
        when(userRepository.save(any(User.class))).thenReturn(activeUser);
        mockBuildAuthResponse();

        authService.verifyOtp(request);

        assertThat(activeUser.isPhoneVerified()).isTrue();
    }

    @Test
    void verifyOtp_whenOtpInvalid_shouldThrow() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmailOrPhone("john@test.com");
        request.setOtp("wrong-otp");

        when(otpService.validateOtp("john@test.com", "wrong-otp")).thenReturn(false);

        assertThatThrownBy(() -> authService.verifyOtp(request))
                .isInstanceOf(InvalidOtpException.class);

        verify(otpService, never()).invalidateOtp(anyString());
    }

    @Test
    void verifyOtp_whenUserNotFound_shouldThrow() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmailOrPhone("unknown@test.com");
        request.setOtp("123456");

        when(otpService.validateOtp("unknown@test.com", "123456")).thenReturn(true);
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.verifyOtp(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void logout_withExistingUser_shouldRevokeTokens() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(activeUser));

        authService.logout("john@test.com");

        verify(refreshTokenService).revokeAllUserTokens(activeUser.getId());
    }

    @Test
    void logout_whenUserNotFound_shouldThrow() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.logout("unknown@test.com"))
                .isInstanceOf(UserNotFoundException.class);

        verify(refreshTokenService, never()).revokeAllUserTokens(anyLong());
    }
}
