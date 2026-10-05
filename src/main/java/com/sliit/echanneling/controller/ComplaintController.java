package com.sliit.echanneling.controller;

import com.sliit.echanneling.dto.request.ComplaintRequestDTO;
import com.sliit.echanneling.model.Complaint;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        if ("PATIENT".equalsIgnoreCase(account.getRole().name()) && account.getPatient() != null) {
            model.addAttribute("complaints", complaintService.getComplaintsByPatient(account.getPatient().getPatientId()));
            model.addAttribute("currentPatientId", account.getPatient().getPatientId());
        } else {
            model.addAttribute("complaints", complaintService.getAllComplaints());
            model.addAttribute("currentPatientId", null);
        }
        return "complaint/list";
    }

    @GetMapping("/new")
    public String newComplaintForm(Authentication authentication, Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        ComplaintRequestDTO request = new ComplaintRequestDTO();
        if (account.getPatient() != null) {
            request.setPatientId(account.getPatient().getPatientId());
        }

        model.addAttribute("complaintForm", request);
        model.addAttribute("categories", complaintService.getAllCategories());
        return "complaint/submit";
    }

    @PostMapping
    public String submitComplaint(@ModelAttribute("complaintForm") ComplaintRequestDTO request,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        var complaint = complaintService.submitComplaint(request);
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        notificationService.sendNotification(account, "Complaint Submitted", "Your complaint Ticket #" + complaint.getComplaintId() + " has been received.");
        redirectAttributes.addFlashAttribute("infoMessage", "Complaint submitted successfully.");
        return "redirect:/complaints";
    }

    @GetMapping("/{id}/edit")
    public String editComplaintForm(@PathVariable("id") Long id,
                                    Authentication authentication,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        if (account.getPatient() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only patients can edit complaints.");
            return "redirect:/complaints";
        }

        try {
            Complaint complaint = complaintService.getComplaintById(id);
            if (!complaint.getPatient().getPatientId().equals(account.getPatient().getPatientId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You are only allowed to update your own complaints.");
                return "redirect:/complaints";
            }

            ComplaintRequestDTO request = new ComplaintRequestDTO();
            request.setComplaintId(complaint.getComplaintId());
            request.setPatientId(complaint.getPatient().getPatientId());
            request.setCategoryId(complaint.getCategory().getCategoryId());
            request.setTitle(complaint.getTitle());
            request.setDescription(complaint.getDescription());

            model.addAttribute("complaintForm", request);
            model.addAttribute("complaintId", id);
            model.addAttribute("categories", complaintService.getAllCategories());
            return "complaint/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/complaints";
        }
    }

    @PutMapping("/{id}")
    public String updateComplaintPut(@PathVariable("id") Long id,
                                     @ModelAttribute("complaintForm") ComplaintRequestDTO request,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        return processUpdateComplaint(id, request, authentication, redirectAttributes, model);
    }

    @PostMapping("/{id}")
    public String updateComplaintPost(@PathVariable("id") Long id,
                                      @ModelAttribute("complaintForm") ComplaintRequestDTO request,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        return processUpdateComplaint(id, request, authentication, redirectAttributes, model);
    }

    private String processUpdateComplaint(Long id,
                                         ComplaintRequestDTO request,
                                         Authentication authentication,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        if (account.getPatient() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only patients can update complaints.");
            return "redirect:/complaints";
        }

        if (request.getTitle() == null || request.getTitle().trim().isEmpty() ||
            request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Title and Description are required fields.");
            model.addAttribute("complaintId", id);
            model.addAttribute("categories", complaintService.getAllCategories());
            return "complaint/edit";
        }

        try {
            complaintService.updateComplaint(id, request, account.getPatient().getPatientId());
            redirectAttributes.addFlashAttribute("infoMessage", "Complaint updated successfully.");
            return "redirect:/complaints";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/complaints";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteComplaintDelete(@PathVariable("id") Long id,
                                        Authentication authentication,
                                        RedirectAttributes redirectAttributes) {
        return processDeleteComplaint(id, authentication, redirectAttributes);
    }

    @PostMapping("/{id}/delete")
    public String deleteComplaintPost(@PathVariable("id") Long id,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {
        return processDeleteComplaint(id, authentication, redirectAttributes);
    }

    private String processDeleteComplaint(Long id,
                                         Authentication authentication,
                                         RedirectAttributes redirectAttributes) {
        UserAccount account = userAccountService.findByUsername(authentication.getName());
        if (account.getPatient() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only patients can delete complaints.");
            return "redirect:/complaints";
        }

        try {
            complaintService.deleteComplaint(id, account.getPatient().getPatientId());
            redirectAttributes.addFlashAttribute("infoMessage", "Complaint deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
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
