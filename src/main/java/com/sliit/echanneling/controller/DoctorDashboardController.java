package com.sliit.echanneling.controller;

import com.sliit.echanneling.model.UserAccount;
import com.sliit.echanneling.service.AppointmentService;
import com.sliit.echanneling.service.ScheduleService;
import com.sliit.echanneling.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorDashboardController {

    private final UserAccountService userAccountService;
    private final ScheduleService scheduleService;
    private final AppointmentService appointmentService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        Long doctorId = account.getStaff().getStaffId();
        model.addAttribute("doctor", account.getStaff());
        model.addAttribute("schedules", scheduleService.getSchedulesByDoctor(doctorId));
        model.addAttribute("appointments", appointmentService.getAppointmentsByDoctor(doctorId));
        return "doctor/dashboard";
    }
}
