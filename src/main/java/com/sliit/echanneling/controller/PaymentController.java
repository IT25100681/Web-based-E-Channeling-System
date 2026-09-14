package com.sliit.echanneling.controller;

import com.sliit.echanneling.dto.request.PaymentRequestDTO;
import com.sliit.echanneling.dto.response.AppointmentViewDTO;
import com.sliit.echanneling.model.Payment;
import com.sliit.echanneling.service.AppointmentService;
import com.sliit.echanneling.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patient/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final AppointmentService appointmentService;
    private final PaymentService paymentService;

    @GetMapping("/checkout/{appointmentId}")
    public String checkout(@PathVariable("appointmentId") Long appointmentId, Model model) {
        AppointmentViewDTO app = appointmentService.getAppointmentById(appointmentId);

        PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
        paymentRequest.setAppointmentId(app.getAppointmentId());
        paymentRequest.setAmount(app.getFee());
        paymentRequest.setPaymentMethod("CARD");

        model.addAttribute("appointment", app);
        model.addAttribute("paymentRequest", paymentRequest);
        return "payment/checkout";
    }

    @PostMapping("/process")
    public String processPayment(@ModelAttribute("paymentRequest") PaymentRequestDTO request, Model model) {
        Payment payment = paymentService.processPayment(request);
        if ("PAID".equalsIgnoreCase(payment.getPaymentStatus().name())) {
            return "redirect:/patient/payments/receipt/" + payment.getAppointment().getAppointmentId();
        } else {
            model.addAttribute("errorMessage", "Payment transaction failed. Please try again with valid card credentials.");
            return "payment/checkout";
        }
    }

    @GetMapping("/receipt/{appointmentId}")
    public String receipt(@PathVariable("appointmentId") Long appointmentId, Model model) {
        Payment payment = paymentService.getPaymentByAppointment(appointmentId);
        model.addAttribute("payment", payment);
        return "payment/receipt";
    }
}
