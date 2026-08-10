package com.example.ecommerce_backend.modules.otp.service;

import com.example.ecommerce_backend.modules.otp.exception.OtpCooldownException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OtpServiceTest {

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService();
    }

    @Test
    void generateOtp_shouldReturnSixDigitCode() {
        String otp = otpService.generateOtp("john@test.com");

        assertThat(otp).matches("\\d{6}");
    }

    @Test
    void generateOtp_shouldStoreOtpForValidation() {
        String otp = otpService.generateOtp("john@test.com");

        assertThat(otpService.validateOtp("john@test.com", otp)).isTrue();
    }

    @Test
    void generateOtp_withinCooldown_shouldThrow() {
        otpService.generateOtp("john@test.com");

        assertThatThrownBy(() -> otpService.generateOtp("john@test.com"))
                .isInstanceOf(OtpCooldownException.class);
    }

    @Test
    void validateOtp_withNonExistentIdentifier_shouldReturnFalse() {
        assertThat(otpService.validateOtp("unknown@test.com", "123456")).isFalse();
    }

    @Test
    void validateOtp_withWrongOtp_shouldReturnFalse() {
        otpService.generateOtp("john@test.com");

        assertThat(otpService.validateOtp("john@test.com", "wrong-otp")).isFalse();
    }

    @Test
    void invalidateOtp_shouldRemoveStoredOtp() {
        otpService.generateOtp("john@test.com");
        otpService.invalidateOtp("john@test.com");

        assertThat(otpService.validateOtp("john@test.com", "any")).isFalse();
    }

    @Test
    void invalidateOtp_withNonExistentIdentifier_shouldNotThrow() {
        otpService.invalidateOtp("unknown@test.com");
    }

    @Test
    void generateOtp_shouldProduceDifferentCodesOnEachCall() {
        String otp1 = otpService.generateOtp("john@test.com");
        String otp2 = otpService.generateOtp("jane@test.com");

        assertThat(otp1).isNotEqualTo(otp2);
    }

    @Test
    void generateOtp_forDifferentIdentifiers_shouldBeIndependent() {
        String otp1 = otpService.generateOtp("user1@test.com");
        String otp2 = otpService.generateOtp("user2@test.com");

        assertThat(otpService.validateOtp("user1@test.com", otp1)).isTrue();
        assertThat(otpService.validateOtp("user2@test.com", otp2)).isTrue();
        assertThat(otpService.validateOtp("user1@test.com", otp2)).isFalse();
        assertThat(otpService.validateOtp("user2@test.com", otp1)).isFalse();
    }
}
