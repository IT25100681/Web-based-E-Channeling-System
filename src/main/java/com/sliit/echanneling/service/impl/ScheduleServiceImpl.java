package com.sliit.echanneling.service.impl;

import com.sliit.echanneling.dto.request.DoctorScheduleRequest;
import com.sliit.echanneling.dto.response.DoctorScheduleResponse;
import com.sliit.echanneling.model.Department;
import com.sliit.echanneling.model.Doctor;
import com.sliit.echanneling.model.DoctorSchedule;
import com.sliit.echanneling.repository.AppointmentRepository;
import com.sliit.echanneling.repository.DepartmentRepository;
import com.sliit.echanneling.repository.DoctorRepository;
import com.sliit.echanneling.repository.DoctorScheduleRepository;
import com.sliit.echanneling.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public DoctorSchedule createSchedule(DoctorScheduleRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + request.getDoctorId()));

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId()).orElse(null);
        } else {
            department = doctor.getDepartment();
        }

        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .department(department)
                .scheduleDate(request.getScheduleDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .maxPatients(request.getMaxPatients())
                .consultationFee(request.getConsultationFee())
                .status("ACTIVE")
                .build();

        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> getSchedulesByDoctor(Long doctorId) {
        return scheduleRepository.findByDoctor_StaffId(doctorId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> searchAvailableSchedules(String specialization, String date) {
        String specQuery = (specialization == null || specialization.isBlank()) ? null : specialization.trim();
        String dateQuery = (date == null || date.isBlank()) ? null : date.trim();
        return scheduleRepository.searchAvailableSchedules(specQuery, dateQuery).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorScheduleResponse getScheduleById(Long scheduleId) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));
        return mapToResponse(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorSchedule getScheduleEntityById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.sliit.echanneling.model.Appointment> getAppointmentsForSchedule(Long scheduleId) {
        return appointmentRepository.findBySchedule_ScheduleId(scheduleId);
    }

    @Override
    @Transactional
    public DoctorSchedule updateSchedule(Long scheduleId, DoctorScheduleRequest request) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));

        if (request.getDoctorId() != null && (schedule.getDoctor() == null || !request.getDoctorId().equals(schedule.getDoctor().getStaffId()))) {
            Doctor doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + request.getDoctorId()));
            schedule.setDoctor(doctor);
        }

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId()).orElse(null);
            schedule.setDepartment(department);
        }

        schedule.setScheduleDate(request.getScheduleDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setMaxPatients(request.getMaxPatients());
        schedule.setConsultationFee(request.getConsultationFee());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            schedule.setStatus(request.getStatus());
        }

        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasBookedAppointments(Long scheduleId) {
        return appointmentRepository.countActiveBookingsBySchedule(scheduleId) > 0;
    }

    @Override
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));

        long activeBookings = appointmentRepository.countActiveBookingsBySchedule(scheduleId);
        if (activeBookings > 0) {
            throw new IllegalStateException("Cannot delete schedule because " + activeBookings + " active appointment(s) are booked for this session. Please cancel the session instead.");
        }

        long totalLinked = appointmentRepository.countBySchedule_ScheduleId(scheduleId);
        if (totalLinked > 0) {
            // Cannot remove physically due to foreign key history; mark cancelled
            schedule.setStatus("CANCELLED");
            scheduleRepository.save(schedule);
        } else {
            scheduleRepository.delete(schedule);
        }
    }

    @Override
    @Transactional
    public void updateScheduleStatus(Long scheduleId, String status) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));
        schedule.setStatus(status);
        scheduleRepository.save(schedule);
    }

    private DoctorScheduleResponse mapToResponse(DoctorSchedule s) {
        long booked = appointmentRepository.countActiveBookingsBySchedule(s.getScheduleId());
        return DoctorScheduleResponse.builder()
                .scheduleId(s.getScheduleId())
                .doctorId(s.getDoctor().getStaffId())
                .doctorName(s.getDoctor().getName())
                .specialization(s.getDoctor().getSpecialization())
                .departmentName(s.getDepartment() != null ? s.getDepartment().getName() : "General")
                .scheduleDate(s.getScheduleDate())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .maxPatients(s.getMaxPatients())
                .bookedCount(booked)
                .consultationFee(s.getConsultationFee())
                .status(s.getStatus())
                .build();
    }
}
