package com.sliit.echanneling.controller;

import com.sliit.echanneling.dto.request.DoctorScheduleRequest;
import com.sliit.echanneling.dto.response.DoctorScheduleResponse;
import com.sliit.echanneling.model.Doctor;
import com.sliit.echanneling.model.DoctorSchedule;
import com.sliit.echanneling.model.UserAccount;
import com.sliit.echanneling.repository.DoctorRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/doctor/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserAccountService userAccountService;
    private final HospitalService hospitalService;
    private final DoctorRepository doctorRepository;

    private boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Long resolveDoctorId(Authentication authentication) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        if (account != null && account.getStaff() != null) {
            return account.getStaff().getStaffId();
        }
        // Fallback for admin or test user
        return doctorRepository.findAll().stream().findFirst().map(Doctor::getStaffId).orElse(null);
    }

    // ==========================================
    // 1. READ: List schedules (Admin views all or filters by doctor; Doctor views own)
    // ==========================================
    @GetMapping
    public String listSchedules(@RequestParam(value = "doctorId", required = false) Long doctorId,
                                Authentication authentication,
                                Model model) {
        boolean admin = isAdmin(authentication);
        List<DoctorScheduleResponse> schedules;

        if (admin) {
            if (doctorId != null && doctorId > 0) {
                schedules = scheduleService.getSchedulesByDoctor(doctorId);
            } else {
                schedules = scheduleService.getAllSchedules();
            }
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("selectedDoctorId", doctorId);
        } else {
            Long docId = resolveDoctorId(authentication);
            schedules = docId != null ? scheduleService.getSchedulesByDoctor(docId) : List.of();
            model.addAttribute("selectedDoctorId", docId);
        }

        long activeCount = schedules.stream().filter(s -> "ACTIVE".equalsIgnoreCase(s.getStatus())).count();
        long totalBookings = schedules.stream().mapToLong(DoctorScheduleResponse::getBookedCount).sum();

        model.addAttribute("schedules", schedules);
        model.addAttribute("totalSchedules", schedules.size());
        model.addAttribute("activeSchedules", activeCount);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("isAdmin", admin);
        return "doctor/schedule-list";
    }

    // ==========================================
    // 2. READ: View single schedule details & booked patients
    // ==========================================
    @GetMapping("/{id}")
    public String viewScheduleDetails(@PathVariable("id") Long scheduleId,
                                      Authentication authentication,
                                      Model model) {
        DoctorScheduleResponse schedule = scheduleService.getScheduleById(scheduleId);
        var appointments = scheduleService.getAppointmentsForSchedule(scheduleId);
        model.addAttribute("schedule", schedule);
        model.addAttribute("appointments", appointments);
        model.addAttribute("isAdmin", isAdmin(authentication));
        return "doctor/schedule-view";
    }

    // ==========================================
    // 3. CREATE: New schedule form
    // ==========================================
    @GetMapping("/new")
    public String newScheduleForm(@RequestParam(value = "doctorId", required = false) Long preselectedDoctorId,
                                  Authentication authentication,
                                  Model model) {
        boolean admin = isAdmin(authentication);
        List<Doctor> doctors = doctorRepository.findAll();

        Long doctorId;
        if (admin) {
            doctorId = preselectedDoctorId != null ? preselectedDoctorId : (doctors.isEmpty() ? null : doctors.get(0).getStaffId());
        } else {
            doctorId = resolveDoctorId(authentication);
        }

        DoctorScheduleRequest req = new DoctorScheduleRequest();
        req.setDoctorId(doctorId);
        req.setMaxPatients(15);
        req.setStatus("ACTIVE");

        model.addAttribute("scheduleForm", req);
        model.addAttribute("departments", hospitalService.getAllDepartments());
        model.addAttribute("doctors", doctors);
        model.addAttribute("isAdmin", admin);
        model.addAttribute("isEdit", false);
        return "doctor/schedule-form";
    }

    // ==========================================
    // 4. CREATE: Save new schedule
    // ==========================================
    @PostMapping
    public String saveSchedule(@Valid @ModelAttribute("scheduleForm") DoctorScheduleRequest request,
                               BindingResult bindingResult,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        boolean admin = isAdmin(authentication);

        if (!admin) {
            // Lock to logged-in doctor
            request.setDoctorId(resolveDoctorId(authentication));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", hospitalService.getAllDepartments());
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("isAdmin", admin);
            model.addAttribute("isEdit", false);
            return "doctor/schedule-form";
        }

        scheduleService.createSchedule(request);
        redirectAttributes.addFlashAttribute("infoMessage", "Channeling schedule session created successfully!");
        return "redirect:/doctor/schedules";
    }

    // ==========================================
    // 5. UPDATE: Edit schedule form
    // ==========================================
    @GetMapping("/{id}/edit")
    public String editScheduleForm(@PathVariable("id") Long scheduleId,
                                   Authentication authentication,
                                   Model model) {
        DoctorSchedule schedule = scheduleService.getScheduleEntityById(scheduleId);
        boolean admin = isAdmin(authentication);

        DoctorScheduleRequest req = new DoctorScheduleRequest();
        req.setScheduleId(schedule.getScheduleId());
        req.setDoctorId(schedule.getDoctor().getStaffId());
        req.setDepartmentId(schedule.getDepartment() != null ? schedule.getDepartment().getDepartmentId() : null);
        req.setScheduleDate(schedule.getScheduleDate());
        req.setStartTime(schedule.getStartTime());
        req.setEndTime(schedule.getEndTime());
        req.setMaxPatients(schedule.getMaxPatients());
        req.setConsultationFee(schedule.getConsultationFee());
        req.setStatus(schedule.getStatus());

        model.addAttribute("scheduleForm", req);
        model.addAttribute("departments", hospitalService.getAllDepartments());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("isAdmin", admin);
        model.addAttribute("isEdit", true);
        model.addAttribute("scheduleId", scheduleId);
        return "doctor/schedule-form";
    }

    // ==========================================
    // 6. UPDATE: Save edited schedule
    // ==========================================
    @PostMapping("/{id}/edit")
    public String updateSchedule(@PathVariable("id") Long scheduleId,
                                 @Valid @ModelAttribute("scheduleForm") DoctorScheduleRequest request,
                                 BindingResult bindingResult,
                                 Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        boolean admin = isAdmin(authentication);

        if (!admin) {
            // Lock to logged-in doctor
            request.setDoctorId(resolveDoctorId(authentication));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", hospitalService.getAllDepartments());
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("isAdmin", admin);
            model.addAttribute("isEdit", true);
            model.addAttribute("scheduleId", scheduleId);
            return "doctor/schedule-form";
        }

        scheduleService.updateSchedule(scheduleId, request);
        redirectAttributes.addFlashAttribute("infoMessage", "Channeling schedule session updated successfully!");
        return "redirect:/doctor/schedules";
    }

    // ==========================================
    // 7. DELETE: Delete schedule (Admin or Doctor)
    // ==========================================
    @PostMapping("/{id}/delete")
    public String deleteSchedule(@PathVariable("id") Long scheduleId, RedirectAttributes redirectAttributes) {
        try {
            scheduleService.deleteSchedule(scheduleId);
            redirectAttributes.addFlashAttribute("infoMessage", "Schedule session removed successfully!");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete schedule: " + ex.getMessage());
        }
        return "redirect:/doctor/schedules";
    }

    // ==========================================
    // 8. STATUS TOGGLE: Activate / Deactivate
    // ==========================================
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") Long scheduleId,
                               @RequestParam("status") String status,
                               RedirectAttributes redirectAttributes) {
        scheduleService.updateScheduleStatus(scheduleId, status);
        redirectAttributes.addFlashAttribute("infoMessage", "Schedule status changed to " + status + ".");
        return "redirect:/doctor/schedules";
    }
}
