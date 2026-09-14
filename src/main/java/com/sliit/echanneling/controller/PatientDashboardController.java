package com.sliit.echanneling.controller;

import com.sliit.echanneling.model.Patient;
import com.sliit.echanneling.service.AppointmentService;
import com.sliit.echanneling.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientDashboardController {

    private final UserAccountService userAccountService;
    private final AppointmentService appointmentService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Patient patient = userAccountService.findPatientByUsername(authentication.getName());
        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointmentService.getAppointmentsByPatient(patient.getPatientId()));
        return "patient/dashboard";
    }
}
