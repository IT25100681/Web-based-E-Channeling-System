package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.PaymentRequestDTO;
import com.sliit.echanneling.model.Payment;

public interface PaymentService {
    Payment processPayment(PaymentRequestDTO request);
    Payment getPaymentByAppointment(Long appointmentId);
}
