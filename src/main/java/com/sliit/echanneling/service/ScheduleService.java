package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.DoctorScheduleRequest;
import com.sliit.echanneling.dto.response.DoctorScheduleResponse;
import com.sliit.echanneling.model.Appointment;
import com.sliit.echanneling.model.DoctorSchedule;

import java.util.List;

public interface ScheduleService {
    // CREATE
    DoctorSchedule createSchedule(DoctorScheduleRequest request);

    // READ
    List<DoctorScheduleResponse> getAllSchedules();
    List<DoctorScheduleResponse> getSchedulesByDoctor(Long doctorId);
    List<DoctorScheduleResponse> searchAvailableSchedules(String specialization, String date);
    DoctorScheduleResponse getScheduleById(Long scheduleId);
    DoctorSchedule getScheduleEntityById(Long scheduleId);
    List<Appointment> getAppointmentsForSchedule(Long scheduleId);

    // UPDATE / EDIT
    DoctorSchedule updateSchedule(Long scheduleId, DoctorScheduleRequest request);
    void updateScheduleStatus(Long scheduleId, String status);

    // DELETE
    void deleteSchedule(Long scheduleId);
    boolean hasBookedAppointments(Long scheduleId);
}
