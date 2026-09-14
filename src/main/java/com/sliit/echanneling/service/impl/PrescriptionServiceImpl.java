package com.sliit.echanneling.service.impl;

import com.sliit.echanneling.dto.request.PrescriptionFormDTO;
import com.sliit.echanneling.dto.request.PrescriptionItemFormDTO;
import com.sliit.echanneling.dto.response.PrescriptionViewDTO;
import com.sliit.echanneling.model.*;
import com.sliit.echanneling.repository.*;
import com.sliit.echanneling.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final MedicationRepository medicationRepository;

    @Override
    @Transactional
    public Prescription createPrescription(PrescriptionFormDTO form) {
        Appointment appointment = appointmentRepository.findById(form.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + form.getAppointmentId()));

        Doctor doctor = doctorRepository.findById(form.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + form.getDoctorId()));

        Patient patient = patientRepository.findById(form.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + form.getPatientId()));

        Prescription prescription = Prescription.builder()
                .appointment(appointment)
                .doctor(doctor)
                .patient(patient)
                .issueDate(LocalDate.now().toString())
                .notes(form.getNotes())
                .items(new ArrayList<>())
                .build();

        for (PrescriptionItemFormDTO itemDTO : form.getItems()) {
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

        return prescriptionRepository.save(prescription);
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
