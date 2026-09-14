package com.sliit.echanneling.controller;

import com.sliit.echanneling.dto.request.PrescriptionFormDTO;
import com.sliit.echanneling.dto.request.PrescriptionItemFormDTO;
import com.sliit.echanneling.dto.response.PrescriptionViewDTO;
import com.sliit.echanneling.model.UserAccount;
import com.sliit.echanneling.service.PrescriptionService;
import com.sliit.echanneling.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final UserAccountService userAccountService;

    @GetMapping
    public String listPrescriptions(Authentication authentication, Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        if ("PATIENT".equalsIgnoreCase(account.getRole().name()) && account.getPatient() != null) {
            model.addAttribute("prescriptions", prescriptionService.getPrescriptionsByPatient(account.getPatient().getPatientId()));
        } else if ("DOCTOR".equalsIgnoreCase(account.getRole().name()) && account.getStaff() != null) {
            model.addAttribute("prescriptions", prescriptionService.getPrescriptionsByDoctor(account.getStaff().getStaffId()));
        } else {
            model.addAttribute("prescriptions", prescriptionService.getAllPrescriptions());
        }
        return "prescription/list";
    }

    @GetMapping("/new")
    public String newPrescriptionForm(@RequestParam("appointmentId") Long appointmentId,
                                      @RequestParam("patientId") Long patientId,
                                      Authentication authentication,
                                      Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        PrescriptionFormDTO form = new PrescriptionFormDTO();
        form.setAppointmentId(appointmentId);
        form.setPatientId(patientId);
        if (account.getStaff() != null) {
            form.setDoctorId(account.getStaff().getStaffId());
        }
        form.getItems().add(new PrescriptionItemFormDTO());

        model.addAttribute("prescriptionForm", form);
        model.addAttribute("medications", prescriptionService.getAllMedications());
        return "prescription/form";
    }

    @PostMapping
    public String savePrescription(@ModelAttribute("prescriptionForm") PrescriptionFormDTO form,
                                   RedirectAttributes redirectAttributes) {
        try {
            prescriptionService.createPrescription(form);
            redirectAttributes.addFlashAttribute("infoMessage", "Digital prescription issued successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to issue prescription: " + e.getMessage());
        }
        return "redirect:/prescriptions";
    }

    @GetMapping("/{id}")
    public String viewPrescription(@PathVariable("id") Long id, Model model) {
        PrescriptionViewDTO dto = prescriptionService.getPrescriptionById(id);
        model.addAttribute("prescription", dto);
        return "prescription/view";
    }

    @GetMapping("/appointment/{appointmentId}")
    public String viewPrescriptionByAppointment(@PathVariable("appointmentId") Long appointmentId,
                                                 Model model,
                                                 RedirectAttributes redirectAttributes) {
        try {
            PrescriptionViewDTO dto = prescriptionService.getPrescriptionByAppointment(appointmentId);
            model.addAttribute("prescription", dto);
            return "prescription/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Prescription not found for this appointment.");
            return "redirect:/prescriptions";
        }
    }
}
