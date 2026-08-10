package com.example.ecommerce_backend.modules.otp.service;

import com.example.ecommerce_backend.modules.otp.exception.OtpAttemptLimitException;
import com.example.ecommerce_backend.modules.otp.exception.OtpCooldownException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private static final long OTP_EXPIRY_MILLIS = 300_000;
    private static final long RESEND_COOLDOWN_MILLIS = 60_000;
    private static final int MAX_RESENDS_PER_WINDOW = 5;
    private static final int MAX_ATTEMPTS = 5;

    public String generateOtp(String identifier) {
        long now = System.currentTimeMillis();
        OtpEntry existing = otpStore.get(identifier);

        if (existing != null && now < existing.expiresAtMs()) {
            if (existing.resendCount() >= MAX_RESENDS_PER_WINDOW) {
                throw new OtpAttemptLimitException("Too many OTP requests. Please try again in a few minutes.");
            }
            if (now < existing.lastSentAt() + RESEND_COOLDOWN_MILLIS) {
                long waitSeconds = (existing.lastSentAt() + RESEND_COOLDOWN_MILLIS - now) / 1000 + 1;
                throw new OtpCooldownException(waitSeconds);
            }
        }

        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        int resendCount = existing == null ? 0 : existing.resendCount() + 1;
        otpStore.put(identifier, new OtpEntry(otp, now + OTP_EXPIRY_MILLIS, now, resendCount, 0));
        return otp;
    }

    public boolean validateOtp(String identifier, String otp) {
        OtpEntry entry = otpStore.get(identifier);
        if (entry == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now > entry.expiresAtMs()) {
            otpStore.remove(identifier);
            return false;
        }
        if (entry.attempts() >= MAX_ATTEMPTS) {
            throw new OtpAttemptLimitException("Too many incorrect attempts. Please request a new OTP.");
        }
        if (!entry.otp().equals(otp)) {
            otpStore.put(identifier, new OtpEntry(
                    entry.otp(), entry.expiresAtMs(), entry.lastSentAt(), entry.resendCount(), entry.attempts() + 1
            ));
            return false;
        }
        return true;
    }

    public void invalidateOtp(String identifier) {
        otpStore.remove(identifier);
    }

    @Scheduled(fixedRate = 60_000)
    public void cleanupExpiredOtps() {
        otpStore.entrySet().removeIf(entry -> System.currentTimeMillis() > entry.getValue().expiresAtMs());
    }

    private record OtpEntry(String otp, long expiresAtMs, long lastSentAt, int resendCount, int attempts) {}
}
