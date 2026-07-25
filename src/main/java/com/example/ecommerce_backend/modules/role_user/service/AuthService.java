package com.example.ecommerce_backend.modules.role_user.service;

import com.example.ecommerce_backend.core.config.JwtTokenProvider;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;

    public AuthService(UserRepository userRepository,
                       RolesRepository rolesRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtTokenProvider jwtTokenProvider,
                       OtpService otpService) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpService = otpService;
    }

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
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        String email = jwtTokenProvider.extractEmail(request.getRefreshToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (!user.isActive()) {
            throw new AccountDeactivatedException();
        }

        return buildAuthResponse(user);
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

    private AuthResponse buildAuthResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        return AuthResponse.builder()
                .token(jwtTokenProvider.generateAccessToken(userDetails))
                .refreshToken(jwtTokenProvider.generateRefreshToken(userDetails))
                .tokenType("Bearer")
                .expiresIn(86400)
                .user(UserMapper.toUserResponse(user))
                .build();
    }
}
