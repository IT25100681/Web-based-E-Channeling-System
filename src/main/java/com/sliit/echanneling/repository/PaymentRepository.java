package com.sliit.echanneling.repository;

import com.sliit.echanneling.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByAppointment_AppointmentId(Long appointmentId);
    Optional<Payment> findByTransactionRef(String transactionRef);
    List<Payment> findByPaymentStatus(com.sliit.echanneling.model.enums.PaymentStatus paymentStatus);
}
