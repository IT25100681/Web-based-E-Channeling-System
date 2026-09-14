package com.sliit.echanneling.controller;

import com.sliit.echanneling.dto.request.RecordFormDTO;
import com.sliit.echanneling.dto.response.MedicalHistoryDTO;
import com.sliit.echanneling.model.UserAccount;
import com.sliit.echanneling.service.MedicalHistoryService;
import com.sliit.echanneling.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/medical-history")
@RequiredArgsConstructor
public class MedicalHistoryController {

    private final MedicalHistoryService historyService;
    private final UserAccountService userAccountService;

    @GetMapping
    public String viewHistory(Authentication authentication, Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        Long patientId = account.getPatient().getPatientId();
        MedicalHistoryDTO history = historyService.getMedicalHistoryByPatient(patientId);
        model.addAttribute("history", history);
        return "medical-history/patient-history";
    }

    @GetMapping("/patient/{patientId}")
    public String viewPatientHistory(@PathVariable("patientId") Long patientId, Model model) {
        MedicalHistoryDTO history = historyService.getMedicalHistoryByPatient(patientId);
        model.addAttribute("history", history);
        return "medical-history/patient-history";
    }

    @GetMapping("/records/new")
    public String newRecordForm(@RequestParam("patientId") Long patientId, Authentication authentication, Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        RecordFormDTO form = new RecordFormDTO();
        form.setPatientId(patientId);
        form.setDoctorId(account.getStaff().getStaffId());

        model.addAttribute("recordForm", form);
        return "medical-history/add-record";
    }

    @PostMapping("/records")
    public String addRecord(@ModelAttribute("recordForm") RecordFormDTO form,
                            @RequestParam(value = "file", required = false) MultipartFile file) {
        var record = historyService.addClinicalRecord(form);
        if (file != null && !file.isEmpty()) {
            historyService.addRecordAttachment(record.getRecordId(), file);
        }
        return "redirect:/medical-history/patient/" + form.getPatientId();
    }
}
