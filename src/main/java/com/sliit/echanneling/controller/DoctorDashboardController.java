package com.sliit.echanneling.controller;

import com.sliit.echanneling.model.Doctor;
import com.sliit.echanneling.model.UserAccount;
import com.sliit.echanneling.repository.DoctorRepository;
import com.sliit.echanneling.service.AppointmentService;
import com.sliit.echanneling.service.ScheduleService;
import com.sliit.echanneling.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorDashboardController {

    private final UserAccountService userAccountService;
    private final ScheduleService scheduleService;
    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        Long staffId = account.getStaff() != null ? account.getStaff().getStaffId() : null;

        Doctor doctor = null;
        if (staffId != null) {
            doctor = doctorRepository.findById(staffId).orElse(null);
        }

        if (doctor == null && account.getStaff() != null) {
            String email = account.getStaff().getEmail();
            doctor = doctorRepository.findAll().stream()
                    .filter(d -> d.getEmail() != null && d.getEmail().equalsIgnoreCase(email))
                    .findFirst()
                    .orElse(null);
        }

        if (doctor == null) {
            doctor = doctorRepository.findAll().stream().findFirst().orElse(null);
        }

        Long docId = doctor != null ? doctor.getStaffId() : staffId;

        model.addAttribute("doctor", doctor);
        model.addAttribute("schedules", docId != null ? scheduleService.getSchedulesByDoctor(docId) : List.of());
        model.addAttribute("appointments", docId != null ? appointmentService.getAppointmentsByDoctor(docId) : List.of());
        return "doctor/dashboard";
    }
}
