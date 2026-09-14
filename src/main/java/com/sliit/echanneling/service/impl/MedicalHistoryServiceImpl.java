package com.sliit.echanneling.service.impl;

import com.sliit.echanneling.dto.request.RecordFormDTO;
import com.sliit.echanneling.dto.response.MedicalHistoryDTO;
import com.sliit.echanneling.model.*;
import com.sliit.echanneling.model.Record;
import com.sliit.echanneling.repository.*;
import com.sliit.echanneling.service.MedicalHistoryService;
import com.sliit.echanneling.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    private final MedicalHistoryRepository historyRepository;
    private final RecordRepository recordRepository;
    private final RecordAttachmentRepository attachmentRepository;
    private final AllergyRepository allergyRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public MedicalHistoryDTO getMedicalHistoryByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));

        MedicalHistory history = historyRepository.findByPatient_PatientId(patientId)
                .orElseGet(() -> historyRepository.save(MedicalHistory.builder()
                        .patient(patient)
                        .createdAt(LocalDate.now().toString())
                        .records(new ArrayList<>())
                        .build()));

        List<Allergy> allergies = allergyRepository.findByPatient_PatientId(patientId);

        List<MedicalHistoryDTO.AllergyView> allergyViews = allergies.stream()
                .map(a -> MedicalHistoryDTO.AllergyView.builder()
                        .allergyId(a.getAllergyId())
                        .allergen(a.getAllergen())
                        .severity(a.getSeverity())
                        .notes(a.getNotes())
                        .build())
                .toList();

        List<MedicalHistoryDTO.RecordView> recordViews = history.getRecords().stream()
                .map(r -> MedicalHistoryDTO.RecordView.builder()
                        .recordId(r.getRecordId())
                        .recordDate(r.getRecordDate())
                        .doctorName(r.getDoctor().getName())
                        .diagnosis(r.getDiagnosis())
                        .treatmentNotes(r.getTreatmentNotes())
                        .attachments(r.getAttachments().stream()
                                .map(att -> MedicalHistoryDTO.AttachmentView.builder()
                                        .attachmentId(att.getAttachmentId())
                                        .fileName(att.getFileName())
                                        .fileType(att.getFileType())
                                        .filePath(att.getFilePath())
                                        .uploadDate(att.getUploadDate())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return MedicalHistoryDTO.builder()
                .historyId(history.getHistoryId())
                .patientId(patient.getPatientId())
                .patientName(patient.getName())
                .bloodGroup(history.getBloodGroup())
                .chronicConditions(history.getChronicConditions())
                .allergies(allergyViews)
                .records(recordViews)
                .build();
    }

    @Override
    @Transactional
    public Record addClinicalRecord(RecordFormDTO form) {
        Patient patient = patientRepository.findById(form.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + form.getPatientId()));

        Doctor doctor = doctorRepository.findById(form.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + form.getDoctorId()));

        MedicalHistory history = historyRepository.findByPatient_PatientId(form.getPatientId())
                .orElseGet(() -> historyRepository.save(MedicalHistory.builder()
                        .patient(patient)
                        .doctor(doctor)
                        .bloodGroup(form.getBloodGroup())
                        .chronicConditions(form.getChronicConditions())
                        .createdAt(LocalDate.now().toString())
                        .records(new ArrayList<>())
                        .build()));

        if (form.getBloodGroup() != null && !form.getBloodGroup().isBlank()) {
            history.setBloodGroup(form.getBloodGroup());
        }
        if (form.getChronicConditions() != null && !form.getChronicConditions().isBlank()) {
            history.setChronicConditions(form.getChronicConditions());
        }
        historyRepository.save(history);

        Record record = Record.builder()
                .medicalHistory(history)
                .doctor(doctor)
                .recordDate(LocalDate.now().toString())
                .diagnosis(form.getDiagnosis())
                .treatmentNotes(form.getTreatmentNotes())
                .attachments(new ArrayList<>())
                .build();

        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public void addRecordAttachment(Long recordId, MultipartFile file) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found: " + recordId));

        String savedPath = fileStorageService.storeFile(file, recordId);

        RecordAttachment attachment = RecordAttachment.builder()
                .record(record)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .filePath(savedPath)
                .uploadDate(LocalDate.now().toString())
                .build();

        attachmentRepository.save(attachment);
    }

    @Override
    @Transactional
    public Allergy addPatientAllergy(Long patientId, String allergen, String severity, String notes) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));

        Allergy allergy = Allergy.builder()
                .patient(patient)
                .allergen(allergen)
                .severity(severity)
                .notes(notes)
                .build();

        return allergyRepository.save(allergy);
    }
}
