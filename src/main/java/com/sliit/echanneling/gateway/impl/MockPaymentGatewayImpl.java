package com.sliit.echanneling.gateway.impl;

import com.sliit.echanneling.dto.request.PaymentRequestDTO;
import com.sliit.echanneling.gateway.PaymentGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MockPaymentGatewayImpl implements PaymentGateway {

    @Override
    public PaymentResult processPayment(PaymentRequestDTO request) {
        // Simulate card validation logic
        if (request.getCardNumber() == null || !request.getCardNumber().matches("^\\d{16}$")) {
            return new PaymentResult(false, null, "Card number must contain exactly 16 digits.");
        }
        if (request.getCardNumber().endsWith("0000")) {
            return new PaymentResult(false, null, "Card declined: Invalid card details");
        }
        String ref = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(true, ref, "Payment approved successfully");
    }
}
