package com.example.ecommerce_backend.core.service;

import com.example.ecommerce_backend.core.config.JwtTokenProvider;
import com.example.ecommerce_backend.core.entity.RefreshToken;
import com.example.ecommerce_backend.core.repository.RefreshTokenRepository;
import com.example.ecommerce_backend.modules.role_user.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Transactional
    public String storeRefreshToken(String rawToken, Long userId) {
        String hash = hashToken(rawToken);
        long expiresInMs = refreshExpirationMs;
        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash)
                .userId(userId)
                .expiresAt(Instant.now().plusMillis(expiresInMs))
                .revoked(false)
                .build();
        refreshTokenRepository.save(entity);
        return rawToken;
    }

    @Transactional
    public String validateAndRotate(String oldRawToken) {
        String hash = hashToken(oldRawToken);

        Optional<RefreshToken> opt = refreshTokenRepository.findByTokenHash(hash);
        if (opt.isEmpty()) {
            throw new InvalidTokenException("Refresh token not found or already revoked");
        }

        RefreshToken stored = opt.get();
        if (stored.isRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked");
        }
        if (stored.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            throw new InvalidTokenException("Refresh token has expired");
        }

        String email = jwtTokenProvider.extractEmail(oldRawToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        stored.setRevoked(true);
        stored.setRevokedAt(Instant.now());
        refreshTokenRepository.save(stored);

        storeRefreshToken(newRefreshToken, stored.getUserId());

        return newRefreshToken;
    }

    @Transactional
    public void revokeAllUserTokens(Long userId) {
        java.util.List<RefreshToken> tokens = refreshTokenRepository.findByUserId(userId);
        Instant now = Instant.now();
        for (RefreshToken token : tokens) {
            if (!token.isRevoked()) {
                token.setRevoked(true);
                token.setRevokedAt(now);
            }
        }
        refreshTokenRepository.saveAll(tokens);
    }

    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredBefore(Instant.now());
    }

    private String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
