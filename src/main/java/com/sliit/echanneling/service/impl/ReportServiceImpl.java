package com.sliit.echanneling.service.impl;

import com.sliit.echanneling.dto.response.RevenueReportDTO;
import com.sliit.echanneling.model.Doctor;
import com.sliit.echanneling.repository.AppointmentRepository;
import com.sliit.echanneling.repository.DoctorRepository;
import com.sliit.echanneling.repository.PatientRepository;
import com.sliit.echanneling.repository.PaymentRepository;
import com.sliit.echanneling.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RevenueReportDTO> getRevenueReport() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<RevenueReportDTO> report = new ArrayList<>();

        for (Doctor doctor : doctors) {
            var appointments = appointmentRepository.findByDoctor_StaffId(doctor.getStaffId());
            long count = appointments.size();
            BigDecimal total = BigDecimal.ZERO;
            for (var app : appointments) {
                if (app.getSchedule() != null && app.getSchedule().getConsultationFee() != null) {
                    total = total.add(app.getSchedule().getConsultationFee());
                }
            }
            report.add(RevenueReportDTO.builder()
                    .doctorName(doctor.getName())
                    .departmentName(doctor.getDepartment() != null ? doctor.getDepartment().getName() : "General")
                    .totalAppointments(count)
                    .totalRevenue(total)
                    .build());
        }

        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalAppointments() {
        return appointmentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalPatients() {
        return patientRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalDoctors() {
        return doctorRepository.count();
    }
}
