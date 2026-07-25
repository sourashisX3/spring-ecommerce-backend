package com.example.ecommerce_backend.modules.role_user.service;

import com.example.ecommerce_backend.core.config.JwtTokenProvider;
import com.example.ecommerce_backend.core.service.RefreshTokenService;
import com.example.ecommerce_backend.modules.role_user.dto.request.LoginRequest;
import com.example.ecommerce_backend.modules.role_user.dto.request.RefreshTokenRequest;
import com.example.ecommerce_backend.modules.role_user.dto.request.RegisterRequest;
import com.example.ecommerce_backend.modules.role_user.dto.request.SendOtpRequest;
import com.example.ecommerce_backend.modules.role_user.dto.request.VerifyOtpRequest;
import com.example.ecommerce_backend.modules.role_user.dto.response.AuthResponse;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import com.example.ecommerce_backend.modules.role_user.entity.UserAddress;
import com.example.ecommerce_backend.modules.role_user.entity.Role;
import com.example.ecommerce_backend.modules.role_user.exception.AccountDeactivatedException;
import com.example.ecommerce_backend.modules.role_user.exception.EmailAlreadyExistsException;
import com.example.ecommerce_backend.modules.role_user.exception.InvalidOtpException;
import com.example.ecommerce_backend.modules.role_user.exception.InvalidTokenException;
import com.example.ecommerce_backend.modules.role_user.exception.PhoneAlreadyExistsException;
import com.example.ecommerce_backend.modules.role_user.exception.RoleNotFoundException;
import com.example.ecommerce_backend.modules.role_user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.role_user.mapper.UserMapper;
import com.example.ecommerce_backend.modules.role_user.repository.RolesRepository;
import com.example.ecommerce_backend.modules.role_user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private OtpService otpService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        if (request.getPhoneNumber() != null && userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new PhoneAlreadyExistsException(request.getPhoneNumber());
        }

        UserAddress address = new UserAddress();
        address.setStreetAddress(request.getStreetAddress());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setZipCode(request.getZipCode());

        Role role;
        if (request.getRoleId() != null) {
            role = rolesRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RoleNotFoundException(request.getRoleId()));
        } else {
            role = rolesRepository.findByRoleName("USER")
                    .orElseGet(() -> rolesRepository.save(Role.builder().roleName("USER").build()));
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .dialCode(request.getDialCode())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(address)
                .role(role)
                .isActive(true)
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .build();

        user = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmailOrPhone(), request.getPassword())
        );

        User user = findUserByIdentifier(request.getEmailOrPhone());

        if (!user.isActive()) {
            throw new AccountDeactivatedException();
        }

        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String oldToken = request.getRefreshToken();
        if (!jwtTokenProvider.validateToken(oldToken)) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        String email = jwtTokenProvider.extractEmail(oldToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (!user.isActive()) {
            throw new AccountDeactivatedException();
        }

        String newRefreshToken = refreshTokenService.validateAndRotate(oldToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(86400)
                .user(UserMapper.toUserResponse(user))
                .build();
    }

    public String sendOtp(SendOtpRequest request) {
        User user = findUserByIdentifier(request.getEmailOrPhone());
        return otpService.generateOtp(request.getEmailOrPhone());
    }

    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        if (!otpService.validateOtp(request.getEmailOrPhone(), request.getOtp())) {
            throw new InvalidOtpException();
        }

        User user = findUserByIdentifier(request.getEmailOrPhone());

        if (request.getEmailOrPhone().contains("@")) {
            user.setEmailVerified(true);
        } else {
            user.setPhoneVerified(true);
        }

        userRepository.save(user);
        otpService.invalidateOtp(request.getEmailOrPhone());
        return buildAuthResponse(user);
    }

    private User findUserByIdentifier(String emailOrPhone) {
        if (emailOrPhone.contains("@")) {
            return userRepository.findByEmail(emailOrPhone)
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + emailOrPhone));
        }
        return userRepository.findByPhoneNumber(emailOrPhone)
                .orElseThrow(() -> new UserNotFoundException("User not found with phone: " + emailOrPhone));
    }

    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        refreshTokenService.revokeAllUserTokens(user.getId());
    }

    private AuthResponse buildAuthResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        refreshTokenService.storeRefreshToken(refreshToken, user.getId());

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400)
                .user(UserMapper.toUserResponse(user))
                .build();
    }
}
