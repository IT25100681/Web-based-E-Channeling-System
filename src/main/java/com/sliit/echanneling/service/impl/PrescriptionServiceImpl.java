package com.sliit.echanneling.service.impl;

import com.sliit.echanneling.dto.request.PrescriptionFormDTO;
import com.sliit.echanneling.dto.request.PrescriptionItemFormDTO;
import com.sliit.echanneling.dto.response.PrescriptionViewDTO;
import com.sliit.echanneling.model.*;
import com.sliit.echanneling.model.enums.AppointmentStatus;
import com.sliit.echanneling.model.enums.Channel;
import com.sliit.echanneling.model.enums.NotificationStatus;
import com.sliit.echanneling.repository.*;
import com.sliit.echanneling.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final MedicationRepository medicationRepository;
    private final NotificationRepository notificationRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional
    public Prescription createPrescription(PrescriptionFormDTO form) {
        Appointment appointment = appointmentRepository.findById(form.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + form.getAppointmentId()));

        Doctor doctor = doctorRepository.findById(form.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + form.getDoctorId()));

        Patient patient = patientRepository.findById(form.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + form.getPatientId()));

        // Check if prescription already exists for this appointment
        Optional<Prescription> existingPrescription = prescriptionRepository.findByAppointment_AppointmentId(form.getAppointmentId());
        Prescription prescription;
        if (existingPrescription.isPresent()) {
            prescription = existingPrescription.get();
            prescription.setNotes(form.getNotes());
            prescription.getItems().clear();
        } else {
            prescription = Prescription.builder()
                    .appointment(appointment)
                    .doctor(doctor)
                    .patient(patient)
                    .issueDate(LocalDate.now().toString())
                    .notes(form.getNotes())
                    .items(new ArrayList<>())
                    .build();
        }

        if (form.getItems() != null) {
            for (PrescriptionItemFormDTO itemDTO : form.getItems()) {
                if (itemDTO == null || itemDTO.getMedicationId() == null) {
                    continue;
                }
                Medication medication = medicationRepository.findById(itemDTO.getMedicationId())
                        .orElseThrow(() -> new IllegalArgumentException("Medication not found: " + itemDTO.getMedicationId()));

                PrescriptionItem item = PrescriptionItem.builder()
                        .prescription(prescription)
                        .medication(medication)
                        .dosage(itemDTO.getDosage())
                        .frequency(itemDTO.getFrequency())
                        .durationDays(itemDTO.getDurationDays())
                        .instructions(itemDTO.getInstructions())
                        .build();

                prescription.getItems().add(item);
            }
        }

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // Update appointment status to COMPLETED
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        // Send Notification to Patient
        userAccountRepository.findByPatient_PatientId(patient.getPatientId()).ifPresent(acc -> {
            Notification notif = Notification.builder()
                    .userAccount(acc)
                    .title("Digital Prescription Issued")
                    .message("Dr. " + doctor.getName() + " has issued a digital prescription for your appointment " + appointment.getReferenceNo() + ".")
                    .channel(Channel.IN_APP)
                    .status(NotificationStatus.UNREAD)
                    .createdAt(LocalDate.now().toString())
                    .build();
            notificationRepository.save(notif);
        });

        return savedPrescription;
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionViewDTO getPrescriptionById(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + prescriptionId));
        return mapToViewDTO(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionViewDTO getPrescriptionByAppointment(Long appointmentId) {
        Prescription prescription = prescriptionRepository.findByAppointment_AppointmentId(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found for appointment: " + appointmentId));
        return mapToViewDTO(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionViewDTO> getPrescriptionsByPatient(Long patientId) {
        return prescriptionRepository.findByPatient_PatientId(patientId).stream()
                .map(this::mapToViewDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionViewDTO> getPrescriptionsByDoctor(Long doctorId) {
        return prescriptionRepository.findByDoctor_StaffId(doctorId).stream()
                .map(this::mapToViewDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionViewDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll().stream()
                .map(this::mapToViewDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medication> getAllMedications() {
        return medicationRepository.findAll();
    }

    private PrescriptionViewDTO mapToViewDTO(Prescription p) {
        List<PrescriptionViewDTO.ItemView> itemViews = p.getItems().stream()
                .map(item -> PrescriptionViewDTO.ItemView.builder()
                        .medicationName(item.getMedication().getName())
                        .brand(item.getMedication().getBrand())
                        .dosage(item.getDosage())
                        .frequency(item.getFrequency())
                        .durationDays(item.getDurationDays())
                        .instructions(item.getInstructions())
                        .build())
                .toList();

        return PrescriptionViewDTO.builder()
                .prescriptionId(p.getPrescriptionId())
                .appointmentId(p.getAppointment().getAppointmentId())
                .referenceNo(p.getAppointment().getReferenceNo())
                .doctorName(p.getDoctor().getName())
                .specialization(p.getDoctor().getSpecialization())
                .patientName(p.getPatient().getName())
                .issueDate(p.getIssueDate())
                .notes(p.getNotes())
                .items(itemViews)
                .build();
    }
}
