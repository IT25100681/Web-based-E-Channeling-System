package com.sliit.echanneling.controller;

import com.sliit.echanneling.dto.request.BookingRequestDTO;
import com.sliit.echanneling.dto.request.RescheduleRequestDTO;
import com.sliit.echanneling.dto.response.AppointmentViewDTO;
import com.sliit.echanneling.dto.response.DoctorScheduleResponse;
import com.sliit.echanneling.model.Appointment;
import com.sliit.echanneling.model.Patient;
import com.sliit.echanneling.service.AppointmentService;
import com.sliit.echanneling.service.HospitalService;
import com.sliit.echanneling.service.ScheduleService;
import com.sliit.echanneling.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/patient/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final ScheduleService scheduleService;
    private final AppointmentService appointmentService;
    private final UserAccountService userAccountService;
    private final HospitalService hospitalService;

    @GetMapping("/search")
    public String searchDoctors(@RequestParam(value = "specialization", required = false) String spec,
                                @RequestParam(value = "date", required = false) String date,
                                Model model) {
        List<DoctorScheduleResponse> schedules = scheduleService.searchAvailableSchedules(spec, date);
        model.addAttribute("schedules", schedules);
        model.addAttribute("specializations", hospitalService.getAllSpecializations());
        model.addAttribute("selectedSpec", spec);
        model.addAttribute("selectedDate", date);
        return "patient/search-doctors";
    }

    @GetMapping("/book/{scheduleId}")
    public String bookForm(@PathVariable("scheduleId") Long scheduleId, Authentication authentication, Model model) {
        Patient patient = userAccountService.findPatientByUsername(authentication.getName());
        DoctorScheduleResponse schedule = scheduleService.getScheduleById(scheduleId);

        BookingRequestDTO request = new BookingRequestDTO();
        request.setScheduleId(scheduleId);
        request.setPatientId(patient.getPatientId());
        request.setAppointmentDate(schedule.getScheduleDate());
        request.setAppointmentTime(schedule.getStartTime());

        model.addAttribute("schedule", schedule);
        model.addAttribute("bookingForm", request);
        return "patient/book-appointment";
    }

    @PostMapping("/book")
    public String submitBooking(@ModelAttribute("bookingForm") BookingRequestDTO request, Model model) {
        try {
            Appointment appointment = appointmentService.bookAppointment(request);
            return "redirect:/patient/payments/checkout/" + appointment.getAppointmentId();
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("schedule", scheduleService.getScheduleById(request.getScheduleId()));
            return "patient/book-appointment";
        }
    }

    @GetMapping
    public String myAppointments(Authentication authentication, Model model) {
        Patient patient = userAccountService.findPatientByUsername(authentication.getName());
        model.addAttribute("appointments", appointmentService.getAppointmentsByPatient(patient.getPatientId()));
        return "patient/my-appointments";
    }

    @GetMapping("/{id}/reschedule")
    public String rescheduleForm(@PathVariable("id") Long id, Authentication authentication, Model model) {
        AppointmentViewDTO appointment = appointmentService.getAppointmentById(id);
        List<DoctorScheduleResponse> schedules = scheduleService.getSchedulesByDoctor(appointment.getDoctorId());

        RescheduleRequestDTO request = new RescheduleRequestDTO();
        request.setAppointmentId(id);
        request.setNewScheduleId(appointment.getScheduleId() != null ? appointment.getScheduleId() : (!schedules.isEmpty() ? schedules.get(0).getScheduleId() : null));
        request.setAppointmentDate(appointment.getAppointmentDate());
        request.setAppointmentTime(appointment.getAppointmentTime());

        model.addAttribute("appointment", appointment);
        model.addAttribute("schedules", schedules);
        model.addAttribute("rescheduleForm", request);
        return "patient/reschedule-appointment";
    }

    @PostMapping("/{id}/reschedule")
    public String submitReschedule(@PathVariable("id") Long id,
                                   @ModelAttribute("rescheduleForm") RescheduleRequestDTO request,
                                   Authentication authentication,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            request.setAppointmentId(id);
            appointmentService.rescheduleAppointment(id, request, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Appointment rescheduled successfully!");
            return "redirect:/patient/appointments";
        } catch (IllegalStateException | IllegalArgumentException e) {
            AppointmentViewDTO appointment = appointmentService.getAppointmentById(id);
            List<DoctorScheduleResponse> schedules = scheduleService.getSchedulesByDoctor(appointment.getDoctorId());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("appointment", appointment);
            model.addAttribute("schedules", schedules);
            model.addAttribute("rescheduleForm", request);
            return "patient/reschedule-appointment";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable("id") Long id, Authentication authentication) {
        appointmentService.cancelAppointment(id, authentication.getName());
        return "redirect:/patient/appointments";
    }
}
