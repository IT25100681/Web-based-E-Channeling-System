package com.sliit.echanneling.gateway;

import com.sliit.echanneling.dto.request.PaymentRequestDTO;

public interface PaymentGateway {
    PaymentResult processPayment(PaymentRequestDTO request);

    record PaymentResult(boolean success, String transactionRef, String message) {}
}
