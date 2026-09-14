package com.sliit.echanneling.controller;

import com.sliit.echanneling.model.Department;
import com.sliit.echanneling.model.Hospital;
import com.sliit.echanneling.model.Specialization;
import com.sliit.echanneling.service.HospitalService;
import com.sliit.echanneling.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ReportService reportService;
    private final HospitalService hospitalService;

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

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("revenueReports", reportService.getRevenueReport());
        return "admin/reports";
    }
}
