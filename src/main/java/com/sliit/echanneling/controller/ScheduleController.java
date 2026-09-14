package com.sliit.echanneling.controller;

import com.sliit.echanneling.dto.request.DoctorScheduleRequest;
import com.sliit.echanneling.model.UserAccount;
import com.sliit.echanneling.service.HospitalService;
import com.sliit.echanneling.service.ScheduleService;
import com.sliit.echanneling.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctor/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserAccountService userAccountService;
    private final HospitalService hospitalService;

    @GetMapping
    public String listSchedules(Authentication authentication, Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        Long doctorId = account.getStaff().getStaffId();
        model.addAttribute("schedules", scheduleService.getSchedulesByDoctor(doctorId));
        return "doctor/schedule-list";
    }

    @GetMapping("/new")
    public String newScheduleForm(Authentication authentication, Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        DoctorScheduleRequest req = new DoctorScheduleRequest();
        req.setDoctorId(account.getStaff().getStaffId());
        req.setMaxPatients(15);
        model.addAttribute("scheduleForm", req);
        model.addAttribute("departments", hospitalService.getAllDepartments());
        return "doctor/schedule-form";
    }

    @PostMapping
    public String saveSchedule(@Valid @ModelAttribute("scheduleForm") DoctorScheduleRequest request,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", hospitalService.getAllDepartments());
            return "doctor/schedule-form";
        }
        scheduleService.createSchedule(request);
        return "redirect:/doctor/schedules";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") Long scheduleId, @RequestParam("status") String status) {
        scheduleService.updateScheduleStatus(scheduleId, status);
        return "redirect:/doctor/schedules";
    }
}
