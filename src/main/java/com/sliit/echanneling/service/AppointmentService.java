package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.BookingRequestDTO;
import com.sliit.echanneling.dto.response.AppointmentViewDTO;
import com.sliit.echanneling.model.Appointment;

import java.util.List;

public interface AppointmentService {
    Appointment bookAppointment(BookingRequestDTO request);
    AppointmentViewDTO getAppointmentByRef(String referenceNo);
    List<AppointmentViewDTO> getAppointmentsByPatient(Long patientId);
    List<AppointmentViewDTO> getAppointmentsByDoctor(Long doctorId);
    void cancelAppointment(Long appointmentId, String username);
    void completeAppointment(Long appointmentId);
}
