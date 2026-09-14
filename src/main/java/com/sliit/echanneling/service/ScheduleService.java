package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.DoctorScheduleRequest;
import com.sliit.echanneling.dto.response.DoctorScheduleResponse;
import com.sliit.echanneling.model.DoctorSchedule;

import java.util.List;

public interface ScheduleService {
    DoctorSchedule createSchedule(DoctorScheduleRequest request);
    List<DoctorScheduleResponse> getSchedulesByDoctor(Long doctorId);
    List<DoctorScheduleResponse> searchAvailableSchedules(String specialization, String date);
    DoctorScheduleResponse getScheduleById(Long scheduleId);
    void updateScheduleStatus(Long scheduleId, String status);
}
