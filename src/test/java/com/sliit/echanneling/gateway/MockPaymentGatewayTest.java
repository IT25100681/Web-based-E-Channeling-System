package com.sliit.echanneling.gateway;

import com.sliit.echanneling.dto.request.PaymentRequestDTO;
import com.sliit.echanneling.gateway.impl.MockPaymentGatewayImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MockPaymentGatewayTest {

    private MockPaymentGatewayImpl paymentGateway;

    @BeforeEach
    void setUp() {
        paymentGateway = new MockPaymentGatewayImpl();
    }

    @Test
    void testProcessPayment_Valid16DigitsCard_Success() {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setAppointmentId(1L);
        request.setAmount(new BigDecimal("1500.00"));
        request.setPaymentMethod("CARD");
        request.setCardNumber("1234567812345678");

        PaymentGateway.PaymentResult result = paymentGateway.processPayment(request);
        assertTrue(result.success());
        assertEquals("Payment approved successfully", result.message());
        assertNotNull(result.transactionRef());
    }

    @Test
    void testProcessPayment_FewerThan16Digits_ReturnsError() {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setCardNumber("12345");

        PaymentGateway.PaymentResult result = paymentGateway.processPayment(request);
        assertFalse(result.success());
        assertEquals("Card number must contain exactly 16 digits.", result.message());
    }

    @Test
    void testProcessPayment_MoreThan16Digits_ReturnsError() {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setCardNumber("12345678123456789");

        PaymentGateway.PaymentResult result = paymentGateway.processPayment(request);
        assertFalse(result.success());
        assertEquals("Card number must contain exactly 16 digits.", result.message());
    }

    @Test
    void testProcessPayment_ContainsLetters_ReturnsError() {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setCardNumber("123456781234567a");

        PaymentGateway.PaymentResult result = paymentGateway.processPayment(request);
        assertFalse(result.success());
        assertEquals("Card number must contain exactly 16 digits.", result.message());
    }

    @Test
    void testProcessPayment_ContainsSpecialCharacters_ReturnsError() {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setCardNumber("1234-5678-9012-34");

        PaymentGateway.PaymentResult result = paymentGateway.processPayment(request);
        assertFalse(result.success());
        assertEquals("Card number must contain exactly 16 digits.", result.message());
    }

    @Test
    void testProcessPayment_NullCardNumber_ReturnsError() {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setCardNumber(null);

        PaymentGateway.PaymentResult result = paymentGateway.processPayment(request);
        assertFalse(result.success());
        assertEquals("Card number must contain exactly 16 digits.", result.message());
    }

    @Test
    void testProcessPayment_CardEndingIn0000_Declines() {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setCardNumber("1234567812340000");

        PaymentGateway.PaymentResult result = paymentGateway.processPayment(request);
        assertFalse(result.success());
        assertEquals("Card declined: Invalid card details", result.message());
    }
}
