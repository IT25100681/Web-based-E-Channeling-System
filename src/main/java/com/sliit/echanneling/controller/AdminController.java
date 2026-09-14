package com.sliit.echanneling.controller;

import com.sliit.echanneling.dto.request.DoctorRegisterDTO;
import com.sliit.echanneling.model.Department;
import com.sliit.echanneling.model.Hospital;
import com.sliit.echanneling.model.Specialization;
import com.sliit.echanneling.service.HospitalService;
import com.sliit.echanneling.service.ReportService;
import com.sliit.echanneling.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ReportService reportService;
    private final HospitalService hospitalService;
    private final UserAccountService userAccountService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalAppointments", reportService.getTotalAppointments());
        model.addAttribute("totalPatients", reportService.getTotalPatients());
        model.addAttribute("totalDoctors", reportService.getTotalDoctors());
        model.addAttribute("revenueReports", reportService.getRevenueReport());
        return "admin/dashboard";
    }

    @GetMapping("/hospitals")
    public String hospitals(Model model) {
        model.addAttribute("hospitals", hospitalService.getAllHospitals());
        model.addAttribute("departments", hospitalService.getAllDepartments());
        model.addAttribute("specializations", hospitalService.getAllSpecializations());
        model.addAttribute("newHospital", new Hospital());
        model.addAttribute("newDepartment", new Department());
        model.addAttribute("newSpecialization", new Specialization());
        return "admin/hospitals";
    }

    @PostMapping("/hospitals")
    public String createHospital(@ModelAttribute Hospital hospital) {
        hospitalService.createHospital(hospital);
        return "redirect:/admin/hospitals";
    }

    @PostMapping("/departments")
    public String createDepartment(@ModelAttribute Department department, @RequestParam Long hospitalId) {
        hospitalService.createDepartment(department, hospitalId);
        return "redirect:/admin/hospitals";
    }

    @PostMapping("/specializations")
    public String createSpecialization(@ModelAttribute Specialization specialization) {
        hospitalService.createSpecialization(specialization);
        return "redirect:/admin/hospitals";
    }

    @GetMapping("/doctors")
    public String doctors(Model model) {
        if (!model.containsAttribute("doctorForm")) {
            model.addAttribute("doctorForm", new DoctorRegisterDTO());
        }
        model.addAttribute("doctors", userAccountService.getAllDoctors());
        model.addAttribute("hospitals", hospitalService.getAllHospitals());
        model.addAttribute("departments", hospitalService.getAllDepartments());
        model.addAttribute("specializations", hospitalService.getAllSpecializations());
        return "admin/doctors";
    }

    @PostMapping("/doctors")
    public String createDoctor(@Valid @ModelAttribute("doctorForm") DoctorRegisterDTO dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.doctorForm", bindingResult);
            redirectAttributes.addFlashAttribute("doctorForm", dto);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add doctor. Please check form errors.");
            return "redirect:/admin/doctors";
        }
        try {
            userAccountService.registerDoctor(dto);
            redirectAttributes.addFlashAttribute("infoMessage", "Doctor registered successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("doctorForm", dto);
        }
        return "redirect:/admin/doctors";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("revenueReports", reportService.getRevenueReport());
        return "admin/reports";
    }
}
