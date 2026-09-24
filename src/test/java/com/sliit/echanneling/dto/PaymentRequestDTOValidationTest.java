package com.sliit.echanneling.dto;

import com.sliit.echanneling.dto.request.PaymentRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRequestDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValid16DigitCardNumber_NoViolations() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setAppointmentId(1L);
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setPaymentMethod("CARD");
        dto.setCardNumber("1234567890123456");

        Set<ConstraintViolation<PaymentRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testCardNumberWithLetters_HasViolation() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setAppointmentId(1L);
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setPaymentMethod("CARD");
        dto.setCardNumber("123456789012345a");

        Set<ConstraintViolation<PaymentRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Card number must contain exactly 16 digits.")));
    }

    @Test
    void testCardNumberWithSpecialChars_HasViolation() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setAppointmentId(1L);
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setPaymentMethod("CARD");
        dto.setCardNumber("1234-5678-9012-34");

        Set<ConstraintViolation<PaymentRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Card number must contain exactly 16 digits.")));
    }

    @Test
    void testCardNumber15Digits_HasViolation() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setAppointmentId(1L);
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setPaymentMethod("CARD");
        dto.setCardNumber("123456789012345");

        Set<ConstraintViolation<PaymentRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Card number must contain exactly 16 digits.")));
    }

    @Test
    void testCardNumber17Digits_HasViolation() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setAppointmentId(1L);
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setPaymentMethod("CARD");
        dto.setCardNumber("12345678901234567");

        Set<ConstraintViolation<PaymentRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Card number must contain exactly 16 digits.")));
    }

    @Test
    void testBlankCardNumber_HasViolation() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setAppointmentId(1L);
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setPaymentMethod("CARD");
        dto.setCardNumber("");

        Set<ConstraintViolation<PaymentRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Card number must contain exactly 16 digits.")));
    }
}
