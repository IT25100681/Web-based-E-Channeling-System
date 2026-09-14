package com.sliit.echanneling.service.impl;

import com.sliit.echanneling.dto.request.PaymentRequestDTO;
import com.sliit.echanneling.gateway.PaymentGateway;
import com.sliit.echanneling.model.Appointment;
import com.sliit.echanneling.model.Payment;
import com.sliit.echanneling.model.enums.AppointmentStatus;
import com.sliit.echanneling.model.enums.PaymentStatus;
import com.sliit.echanneling.model.enums.PaymentType;
import com.sliit.echanneling.repository.AppointmentRepository;
import com.sliit.echanneling.repository.PaymentRepository;
import com.sliit.echanneling.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentGateway paymentGateway;

    @Override
    @Transactional
    public Payment processPayment(PaymentRequestDTO request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + request.getAppointmentId()));

        PaymentGateway.PaymentResult result = paymentGateway.processPayment(request);

        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Payment payment = Payment.builder()
                .appointment(appointment)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(result.success() ? PaymentStatus.PAID : PaymentStatus.FAILED)
                .transactionRef(result.success() ? result.transactionRef() : "FAILED-" + System.currentTimeMillis())
                .paymentType(PaymentType.PAYMENT)
                .createdAt(nowStr)
                .build();

        payment = paymentRepository.save(payment);

        if (result.success()) {
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            appointmentRepository.save(appointment);
        }

        return payment;
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByAppointment(Long appointmentId) {
        return paymentRepository.findByAppointment_AppointmentId(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found for appointment: " + appointmentId));
    }
}
