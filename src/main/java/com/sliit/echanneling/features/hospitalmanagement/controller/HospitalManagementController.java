package com.sliit.echanneling.features.hospitalmanagement.controller;

import com.sliit.echanneling.features.hospitalmanagement.dto.DepartmentForm;
import com.sliit.echanneling.features.hospitalmanagement.dto.HospitalForm;
import com.sliit.echanneling.features.hospitalmanagement.dto.SpecializationForm;
import com.sliit.echanneling.features.hospitalmanagement.dto.SystemSettingForm;
import com.sliit.echanneling.features.hospitalmanagement.exception.HospitalManagementException;
import com.sliit.echanneling.features.hospitalmanagement.service.HospitalManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/hospital-management")
@RequiredArgsConstructor
public class HospitalManagementController {

    private final HospitalManagementService hospitalManagementService;

    @GetMapping
    public String index(@RequestParam(required = false) String q, Model model) {
        seedForms(model);
        model.addAttribute("query", q);
        model.addAttribute("hospitals", hospitalManagementService.getHospitals(q));
        model.addAttribute("departments", hospitalManagementService.getDepartments(q));
        model.addAttribute("specializations", hospitalManagementService.getSpecializations(q));
        model.addAttribute("settings", hospitalManagementService.getSettings());
        model.addAttribute("activityLogs", hospitalManagementService.getRecentLogs());
        return "features/hospital-management/index";
    }

    @PostMapping("/hospitals")
    public String createHospital(@Valid @ModelAttribute("hospitalForm") HospitalForm form,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            preserveErrors("hospitalForm", form, bindingResult, redirectAttributes, "Please correct the hospital form.");
            return redirect("hospitals");
        }
        return runWrite(() -> hospitalManagementService.createHospital(form), redirectAttributes, "Hospital saved.", "hospitals");
    }

    @PostMapping("/hospitals/{id}")
    public String updateHospital(@PathVariable Long id,
                                 @Valid @ModelAttribute("hospitalForm") HospitalForm form,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            preserveErrors("hospitalForm", form, bindingResult, redirectAttributes, "Please correct the hospital form.");
            return redirect("hospitals");
        }
        return runWrite(() -> hospitalManagementService.updateHospital(id, form), redirectAttributes, "Hospital updated.", "hospitals");
    }

    @PostMapping("/hospitals/{id}/deactivate")
    public String deactivateHospital(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runWrite(() -> hospitalManagementService.deactivateHospital(id), redirectAttributes, "Hospital deactivated.", "hospitals");
    }

    @PostMapping("/hospitals/{id}/restore")
    public String restoreHospital(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runWrite(() -> hospitalManagementService.restoreHospital(id), redirectAttributes, "Hospital restored.", "hospitals");
    }

    @PostMapping("/hospitals/{id}/delete")
    public String deleteHospital(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runWrite(() -> hospitalManagementService.deleteHospital(id), redirectAttributes, "Hospital safely deactivated.", "hospitals");
    }

    @PostMapping("/departments")
    public String createDepartment(@Valid @ModelAttribute("departmentForm") DepartmentForm form,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            preserveErrors("departmentForm", form, bindingResult, redirectAttributes, "Please correct the department form.");
            return redirect("departments");
        }
        return runWrite(() -> hospitalManagementService.createDepartment(form), redirectAttributes, "Department saved.", "departments");
    }

    @PostMapping("/departments/{id}")
    public String updateDepartment(@PathVariable Long id,
                                   @Valid @ModelAttribute("departmentForm") DepartmentForm form,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            preserveErrors("departmentForm", form, bindingResult, redirectAttributes, "Please correct the department form.");
            return redirect("departments");
        }
        return runWrite(() -> hospitalManagementService.updateDepartment(id, form), redirectAttributes, "Department updated.", "departments");
    }

    @PostMapping("/departments/{id}/deactivate")
    public String deactivateDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runWrite(() -> hospitalManagementService.deactivateDepartment(id), redirectAttributes, "Department deactivated.", "departments");
    }

    @PostMapping("/departments/{id}/restore")
    public String restoreDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runWrite(() -> hospitalManagementService.restoreDepartment(id), redirectAttributes, "Department restored.", "departments");
    }

    @PostMapping("/departments/{id}/delete")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runWrite(() -> hospitalManagementService.deleteDepartment(id), redirectAttributes, "Department safely deactivated.", "departments");
    }

    @PostMapping("/specializations")
    public String createSpecialization(@Valid @ModelAttribute("specializationForm") SpecializationForm form,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            preserveErrors("specializationForm", form, bindingResult, redirectAttributes, "Please correct the specialization form.");
            return redirect("specializations");
        }
        return runWrite(() -> hospitalManagementService.createSpecialization(form), redirectAttributes, "Specialization saved.", "specializations");
    }

    @PostMapping("/specializations/{id}")
    public String updateSpecialization(@PathVariable Long id,
                                       @Valid @ModelAttribute("specializationForm") SpecializationForm form,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            preserveErrors("specializationForm", form, bindingResult, redirectAttributes, "Please correct the specialization form.");
            return redirect("specializations");
        }
        return runWrite(() -> hospitalManagementService.updateSpecialization(id, form), redirectAttributes, "Specialization updated.", "specializations");
    }

    @PostMapping("/specializations/{id}/deactivate")
    public String deactivateSpecialization(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runWrite(() -> hospitalManagementService.deactivateSpecialization(id), redirectAttributes, "Specialization deactivated.", "specializations");
    }

    @PostMapping("/specializations/{id}/restore")
    public String restoreSpecialization(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runWrite(() -> hospitalManagementService.restoreSpecialization(id), redirectAttributes, "Specialization restored.", "specializations");
    }

    @PostMapping("/specializations/{id}/delete")
    public String deleteSpecialization(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runWrite(() -> hospitalManagementService.deleteSpecialization(id), redirectAttributes, "Specialization safely deactivated.", "specializations");
    }

    @PostMapping("/settings")
    public String saveSetting(@Valid @ModelAttribute("settingForm") SystemSettingForm form,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            preserveErrors("settingForm", form, bindingResult, redirectAttributes, "Please correct the setting form.");
            return redirect("settings");
        }
        return runWrite(() -> hospitalManagementService.saveSetting(form), redirectAttributes, "Setting saved.", "settings");
    }

    @PostMapping("/operations/backup")
    public String recordBackup(RedirectAttributes redirectAttributes) {
        return runWrite(hospitalManagementService::recordBackup, redirectAttributes, "Backup checkpoint recorded.", "operations");
    }

    @PostMapping("/operations/recovery")
    public String recordRecovery(RedirectAttributes redirectAttributes) {
        return runWrite(hospitalManagementService::recordRecovery, redirectAttributes, "Recovery checkpoint recorded.", "operations");
    }

    private void seedForms(Model model) {
        if (!model.containsAttribute("hospitalForm")) {
            model.addAttribute("hospitalForm", new HospitalForm());
        }
        if (!model.containsAttribute("departmentForm")) {
            model.addAttribute("departmentForm", new DepartmentForm());
        }
        if (!model.containsAttribute("specializationForm")) {
            model.addAttribute("specializationForm", new SpecializationForm());
        }
        if (!model.containsAttribute("settingForm")) {
            model.addAttribute("settingForm", new SystemSettingForm());
        }
    }

    private void preserveErrors(String name, Object form, BindingResult bindingResult,
                                RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult." + name, bindingResult);
        redirectAttributes.addFlashAttribute(name, form);
        redirectAttributes.addFlashAttribute("errorMessage", message);
    }

    private String runWrite(Runnable action, RedirectAttributes redirectAttributes, String successMessage, String tab) {
        try {
            action.run();
            redirectAttributes.addFlashAttribute("infoMessage", successMessage);
        } catch (HospitalManagementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Database operation failed. Please retry or contact an administrator.");
        }
        return redirect(tab);
    }

    private String redirect(String tab) {
        return "redirect:/admin/hospital-management#" + tab;
    }
}
