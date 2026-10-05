package com.sliit.echanneling.controller;

import com.sliit.echanneling.dto.request.RecordFormDTO;
import com.sliit.echanneling.dto.response.MedicalHistoryDTO;
import com.sliit.echanneling.model.Record;
import com.sliit.echanneling.model.UserAccount;
import com.sliit.echanneling.repository.PatientRepository;
import com.sliit.echanneling.service.MedicalHistoryService;
import com.sliit.echanneling.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/medical-history")
@RequiredArgsConstructor
public class MedicalHistoryController {

    private final MedicalHistoryService historyService;
    private final UserAccountService userAccountService;
    private final PatientRepository patientRepository;

    private boolean isAuthorizedStaffOrDoctor(UserAccount account) {
        if (account == null || account.getRole() == null) return false;
        String roleStr = account.getRole().name().toUpperCase();
        return roleStr.contains("DOCTOR") || roleStr.contains("ADMIN") || roleStr.contains("STAFF") || roleStr.contains("RECEPTIONIST");
    }

    @GetMapping
    public String viewHistory(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());

        if (account.getPatient() != null) {
            Long patientId = account.getPatient().getPatientId();
            MedicalHistoryDTO history = historyService.getMedicalHistoryByPatient(patientId);
            model.addAttribute("history", history);
            model.addAttribute("isStaffOrDoctor", false);
            return "medical-history/patient-history";
        }

        var patients = patientRepository.findAll();
        model.addAttribute("patients", patients);
        model.addAttribute("isStaffOrDoctor", true);

        if (!patients.isEmpty()) {
            Long patientId = patients.get(0).getPatientId();
            MedicalHistoryDTO history = historyService.getMedicalHistoryByPatient(patientId);
            model.addAttribute("history", history);
        } else {
            model.addAttribute("infoMessage", "No patients found in system.");
        }
        return "medical-history/patient-history";
    }

    @GetMapping("/patient/{patientId}")
    public String viewPatientHistory(@PathVariable("patientId") Long patientId,
                                     Authentication authentication,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());

        if (account.getPatient() != null && !account.getPatient().getPatientId().equals(patientId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized: You are only allowed to view your own medical history.");
            return "redirect:/medical-history";
        }

        try {
            MedicalHistoryDTO history = historyService.getMedicalHistoryByPatient(patientId);
            model.addAttribute("history", history);
            boolean isStaffOrDoctor = isAuthorizedStaffOrDoctor(account);
            model.addAttribute("isStaffOrDoctor", isStaffOrDoctor);
            if (isStaffOrDoctor) {
                model.addAttribute("patients", patientRepository.findAll());
            }
            return "medical-history/patient-history";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/medical-history";
        }
    }

    @GetMapping("/records/new")
    public String newRecordForm(@RequestParam("patientId") Long patientId,
                                Authentication authentication,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());

        if (!isAuthorizedStaffOrDoctor(account)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access. Only doctors and authorized staff can add clinical records.");
            return "redirect:/medical-history";
        }

        RecordFormDTO form = new RecordFormDTO();
        form.setPatientId(patientId);
        if (account.getStaff() != null) {
            form.setDoctorId(account.getStaff().getStaffId());
        }

        MedicalHistoryDTO history = historyService.getMedicalHistoryByPatient(patientId);

        model.addAttribute("recordForm", form);
        model.addAttribute("patientName", history.getPatientName());
        model.addAttribute("currentBloodGroup", history.getBloodGroup());
        model.addAttribute("currentChronicConditions", history.getChronicConditions());
        return "medical-history/add-record";
    }

    @PostMapping("/records")
    public String addRecord(@ModelAttribute("recordForm") RecordFormDTO form,
                            @RequestParam(value = "file", required = false) MultipartFile file,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());

        if (!isAuthorizedStaffOrDoctor(account)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access. Only doctors and authorized staff can add clinical records.");
            return "redirect:/medical-history";
        }

        if (form.getDiagnosis() == null || form.getDiagnosis().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Diagnosis is a required field.");
            model.addAttribute("recordForm", form);
            return "medical-history/add-record";
        }

        try {
            var record = historyService.addClinicalRecord(form);
            if (file != null && !file.isEmpty()) {
                historyService.addRecordAttachment(record.getRecordId(), file);
            }
            redirectAttributes.addFlashAttribute("infoMessage", "Clinical record added successfully.");
            return "redirect:/medical-history/patient/" + form.getPatientId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/medical-history/patient/" + form.getPatientId();
        }
    }

    @GetMapping("/records/{id}/edit")
    public String editRecordForm(@PathVariable("id") Long id,
                                 Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());

        if (!isAuthorizedStaffOrDoctor(account)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access. Only doctors and authorized staff can edit clinical records.");
            return "redirect:/medical-history";
        }

        try {
            Record record = historyService.getRecordById(id);
            RecordFormDTO form = new RecordFormDTO();
            form.setRecordId(record.getRecordId());
            form.setPatientId(record.getMedicalHistory().getPatient().getPatientId());
            form.setDoctorId(record.getDoctor() != null ? record.getDoctor().getStaffId() : null);
            form.setDiagnosis(record.getDiagnosis());
            form.setTreatmentNotes(record.getTreatmentNotes());
            form.setBloodGroup(record.getMedicalHistory().getBloodGroup());
            form.setChronicConditions(record.getMedicalHistory().getChronicConditions());

            model.addAttribute("recordForm", form);
            model.addAttribute("recordId", id);
            model.addAttribute("patientName", record.getMedicalHistory().getPatient().getName());
            return "medical-history/edit-record";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/medical-history";
        }
    }

    @PutMapping("/records/{id}")
    public String updateRecordPut(@PathVariable("id") Long id,
                                  @ModelAttribute("recordForm") RecordFormDTO form,
                                  @RequestParam(value = "file", required = false) MultipartFile file,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        return processUpdateRecord(id, form, file, authentication, redirectAttributes, model);
    }

    @PostMapping("/records/{id}")
    public String updateRecordPost(@PathVariable("id") Long id,
                                   @ModelAttribute("recordForm") RecordFormDTO form,
                                   @RequestParam(value = "file", required = false) MultipartFile file,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        return processUpdateRecord(id, form, file, authentication, redirectAttributes, model);
    }

    private String processUpdateRecord(Long id,
                                       RecordFormDTO form,
                                       MultipartFile file,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());

        if (!isAuthorizedStaffOrDoctor(account)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access. Only doctors and authorized staff can edit clinical records.");
            return "redirect:/medical-history";
        }

        if (form.getDiagnosis() == null || form.getDiagnosis().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Diagnosis is a required field.");
            model.addAttribute("recordForm", form);
            model.addAttribute("recordId", id);
            return "medical-history/edit-record";
        }

        try {
            Record updated = historyService.updateClinicalRecord(id, form);
            if (file != null && !file.isEmpty()) {
                historyService.addRecordAttachment(updated.getRecordId(), file);
            }
            redirectAttributes.addFlashAttribute("infoMessage", "Clinical record updated successfully.");
            return "redirect:/medical-history/patient/" + form.getPatientId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/medical-history/patient/" + form.getPatientId();
        }
    }

    @DeleteMapping("/records/{id}")
    public String deleteRecordDelete(@PathVariable("id") Long id,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        return processDeleteRecord(id, authentication, redirectAttributes);
    }

    @PostMapping("/records/{id}/delete")
    public String deleteRecordPost(@PathVariable("id") Long id,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        return processDeleteRecord(id, authentication, redirectAttributes);
    }

    private String processDeleteRecord(Long id,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());

        if (!isAuthorizedStaffOrDoctor(account)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access. Only doctors and authorized staff can delete clinical records.");
            return "redirect:/medical-history";
        }

        try {
            Record record = historyService.getRecordById(id);
            Long patientId = record.getMedicalHistory().getPatient().getPatientId();
            historyService.deleteClinicalRecord(id);
            redirectAttributes.addFlashAttribute("infoMessage", "Clinical record deleted successfully.");
            return "redirect:/medical-history/patient/" + patientId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/medical-history";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteMedicalHistoryDelete(@PathVariable("id") Long id,
                                             Authentication authentication,
                                             RedirectAttributes redirectAttributes) {
        return processDeleteMedicalHistory(id, authentication, redirectAttributes);
    }

    @PostMapping("/{id}/delete")
    public String deleteMedicalHistoryPost(@PathVariable("id") Long id,
                                          Authentication authentication,
                                          RedirectAttributes redirectAttributes) {
        return processDeleteMedicalHistory(id, authentication, redirectAttributes);
    }

    private String processDeleteMedicalHistory(Long id,
                                               Authentication authentication,
                                               RedirectAttributes redirectAttributes) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());

        if (!isAuthorizedStaffOrDoctor(account)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access. Only doctors and authorized staff can delete medical history records.");
            return "redirect:/medical-history";
        }

        try {
            historyService.deleteMedicalHistory(id);
            redirectAttributes.addFlashAttribute("infoMessage", "Medical history record deleted successfully from database.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/medical-history";
    }

    @PostMapping("/allergy")
    public String addAllergy(@RequestParam("patientId") Long patientId,
                             @RequestParam("allergen") String allergen,
                             @RequestParam("severity") String severity,
                             @RequestParam(value = "notes", required = false) String notes,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());

        if (!isAuthorizedStaffOrDoctor(account)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access. Only doctors and staff can add allergies.");
            return "redirect:/medical-history/patient/" + patientId;
        }

        try {
            historyService.addPatientAllergy(patientId, allergen, severity, notes);
            redirectAttributes.addFlashAttribute("infoMessage", "Allergy added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/medical-history/patient/" + patientId;
    }

    @DeleteMapping("/allergy/{id}")
    public String deleteAllergyDelete(@PathVariable("id") Long allergyId,
                                      @RequestParam("patientId") Long patientId,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        return processDeleteAllergy(allergyId, patientId, authentication, redirectAttributes);
    }

    @PostMapping("/allergy/{id}/delete")
    public String deleteAllergyPost(@PathVariable("id") Long allergyId,
                                    @RequestParam("patientId") Long patientId,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        return processDeleteAllergy(allergyId, patientId, authentication, redirectAttributes);
    }

    private String processDeleteAllergy(Long allergyId,
                                       Long patientId,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());

        if (!isAuthorizedStaffOrDoctor(account)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access. Only doctors and staff can remove allergies.");
            return "redirect:/medical-history/patient/" + patientId;
        }

        try {
            historyService.deletePatientAllergy(allergyId);
            redirectAttributes.addFlashAttribute("infoMessage", "Allergy removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/medical-history/patient/" + patientId;
    }
}
