package com.sliit.echanneling.controller;

import com.sliit.echanneling.dto.request.ComplaintRequestDTO;
import com.sliit.echanneling.model.UserAccount;
import com.sliit.echanneling.model.enums.ComplaintStatus;
import com.sliit.echanneling.service.ComplaintService;
import com.sliit.echanneling.service.NotificationService;
import com.sliit.echanneling.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserAccountService userAccountService;
    private final NotificationService notificationService;

    @GetMapping
    public String listComplaints(Authentication authentication, Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        if ("PATIENT".equalsIgnoreCase(account.getRole().name())) {
            model.addAttribute("complaints", complaintService.getComplaintsByPatient(account.getPatient().getPatientId()));
        } else {
            model.addAttribute("complaints", complaintService.getAllComplaints());
        }
        return "complaint/list";
    }

    @GetMapping("/new")
    public String newComplaintForm(Authentication authentication, Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        ComplaintRequestDTO request = new ComplaintRequestDTO();
        request.setPatientId(account.getPatient().getPatientId());

        model.addAttribute("complaintForm", request);
        model.addAttribute("categories", complaintService.getAllCategories());
        return "complaint/submit";
    }

    @PostMapping
    public String submitComplaint(@ModelAttribute("complaintForm") ComplaintRequestDTO request, Authentication authentication) {
        var complaint = complaintService.submitComplaint(request);
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        notificationService.sendNotification(account, "Complaint Submitted", "Your complaint Ticket #" + complaint.getComplaintId() + " has been received.");
        return "redirect:/complaints";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("status") ComplaintStatus status,
                               @RequestParam(value = "notes", required = false) String notes,
                               Authentication authentication) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        Long staffId = account.getStaff() != null ? account.getStaff().getStaffId() : null;
        complaintService.updateComplaintStatus(id, status, staffId, notes);
        return "redirect:/complaints";
    }
}
