package com.example.ecommerce_backend.modules.role_user.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private static final long OTP_EXPIRY_SECONDS = 300;

    public String generateOtp(String identifier) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(identifier, new OtpEntry(otp, LocalDateTime.now().plusSeconds(OTP_EXPIRY_SECONDS)));
        return otp;
    }

    public boolean validateOtp(String identifier, String otp) {
        OtpEntry entry = otpStore.get(identifier);
        if (entry == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(entry.expiresAt())) {
            otpStore.remove(identifier);
            return false;
        }
        return entry.otp().equals(otp);
    }

    public void invalidateOtp(String identifier) {
        otpStore.remove(identifier);
    }

    @Scheduled(fixedRate = 60_000)
    public void cleanupExpiredOtps() {
        otpStore.entrySet().removeIf(entry -> LocalDateTime.now().isAfter(entry.getValue().expiresAt()));
    }

    private record OtpEntry(String otp, LocalDateTime expiresAt) {}
}
