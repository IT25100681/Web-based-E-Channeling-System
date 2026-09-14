package com.sliit.echanneling.service.impl;

import com.sliit.echanneling.dto.request.BookingRequestDTO;
import com.sliit.echanneling.dto.response.AppointmentViewDTO;
import com.sliit.echanneling.model.Appointment;
import com.sliit.echanneling.model.DoctorSchedule;
import com.sliit.echanneling.model.Patient;
import com.sliit.echanneling.model.enums.AppointmentStatus;
import com.sliit.echanneling.repository.AppointmentRepository;
import com.sliit.echanneling.repository.DoctorScheduleRepository;
import com.sliit.echanneling.repository.PatientRepository;
import com.sliit.echanneling.repository.PaymentRepository;
import com.sliit.echanneling.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorScheduleRepository scheduleRepository;
    private final PatientRepository patientRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Appointment bookAppointment(BookingRequestDTO request) {
        DoctorSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + request.getScheduleId()));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + request.getPatientId()));

        long activeCount = appointmentRepository.countActiveBookingsBySchedule(schedule.getScheduleId());
        if (activeCount >= schedule.getMaxPatients()) {
            throw new IllegalStateException("This schedule session is fully booked!");
        }

        String refNo = "APP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String date = request.getAppointmentDate() != null ? request.getAppointmentDate() : schedule.getScheduleDate();
        String time = request.getAppointmentTime() != null ? request.getAppointmentTime() : schedule.getStartTime();
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(schedule.getDoctor())
                .schedule(schedule)
                .appointmentDate(date)
                .appointmentTime(time)
                .status(AppointmentStatus.PENDING)
                .referenceNo(refNo)
                .createdAt(nowStr)
                .build();

        try {
            return appointmentRepository.save(appointment);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("That doctor slot is already booked! Please select another time.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentViewDTO getAppointmentByRef(String referenceNo) {
        Appointment appointment = appointmentRepository.findByReferenceNo(referenceNo)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + referenceNo));
        return mapToViewDTO(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentViewDTO> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatient_PatientId(patientId).stream()
                .map(this::mapToViewDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentViewDTO> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctor_StaffId(doctorId).stream()
                .map(this::mapToViewDTO)
                .toList();
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId, String username) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public void completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
    }

    private AppointmentViewDTO mapToViewDTO(Appointment a) {
        String payStatus = paymentRepository.findByAppointment_AppointmentId(a.getAppointmentId())
                .map(p -> p.getPaymentStatus().name())
                .orElse("UNPAID");

        return AppointmentViewDTO.builder()
                .appointmentId(a.getAppointmentId())
                .referenceNo(a.getReferenceNo())
                .patientId(a.getPatient().getPatientId())
                .patientName(a.getPatient().getName())
                .patientEmail(a.getPatient().getEmail())
                .doctorId(a.getDoctor().getStaffId())
                .doctorName(a.getDoctor().getName())
                .specialization(a.getDoctor().getSpecialization())
                .appointmentDate(a.getAppointmentDate())
                .appointmentTime(a.getAppointmentTime())
                .status(a.getStatus())
                .fee(a.getSchedule().getConsultationFee())
                .paymentStatus(payStatus)
                .createdAt(a.getCreatedAt())
                .build();
    }
}
