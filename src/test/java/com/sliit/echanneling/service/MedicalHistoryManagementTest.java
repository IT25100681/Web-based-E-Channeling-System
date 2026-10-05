package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.RecordFormDTO;
import com.sliit.echanneling.dto.response.MedicalHistoryDTO;
import com.sliit.echanneling.model.*;
import com.sliit.echanneling.model.Record;
import com.sliit.echanneling.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MedicalHistoryManagementTest {

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private AllergyRepository allergyRepository;

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    private Patient patient;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        patient = patientRepository.save(Patient.builder()
                .name("Med Patient Test")
                .email("medpatient@test.com")
                .phone("+94779998877")
                .nic("999999999V")
                .build());

        doctor = doctorRepository.findAll().stream().findFirst().orElseGet(() -> {
            Doctor doc = new Doctor();
            doc.setName("Dr. Medical Expert");
            doc.setEmail("doctormed@test.com");
            doc.setPhone("+94771234567");
            doc.setSpecialization("General Practice");
            doc.setLicenseNo("SLMC-998877");
            return doctorRepository.save(doc);
        });
    }

    @Test
    void testCreateClinicalRecordSuccess() {
        RecordFormDTO form = new RecordFormDTO();
        form.setPatientId(patient.getPatientId());
        form.setDoctorId(doctor.getStaffId());
        form.setBloodGroup("O+");
        form.setChronicConditions("Asthma");
        form.setDiagnosis("Acute Bronchitis");
        form.setTreatmentNotes("Prescribed Inhaler & Antibiotics");

        Record created = medicalHistoryService.addClinicalRecord(form);

        assertNotNull(created);
        assertNotNull(created.getRecordId());
        assertEquals("Acute Bronchitis", created.getDiagnosis());

        Record dbRecord = recordRepository.findById(created.getRecordId()).orElseThrow();
        assertEquals("Acute Bronchitis", dbRecord.getDiagnosis());
        assertEquals("Prescribed Inhaler & Antibiotics", dbRecord.getTreatmentNotes());
    }

    @Test
    void testViewMedicalHistoryByPatient() {
        RecordFormDTO form = new RecordFormDTO();
        form.setPatientId(patient.getPatientId());
        form.setDoctorId(doctor.getStaffId());
        form.setBloodGroup("B+");
        form.setChronicConditions("Hypertension");
        form.setDiagnosis("Hypertensive Crisis Checkup");
        form.setTreatmentNotes("Medication adjusted");

        medicalHistoryService.addClinicalRecord(form);
        medicalHistoryService.addPatientAllergy(patient.getPatientId(), "Penicillin", "Severe", "Skin rash");

        MedicalHistoryDTO history = medicalHistoryService.getMedicalHistoryByPatient(patient.getPatientId());

        assertNotNull(history);
        assertEquals("Med Patient Test", history.getPatientName());
        assertEquals("B+", history.getBloodGroup());
        assertEquals("Hypertension", history.getChronicConditions());
        assertEquals(1, history.getAllergies().size());
        assertEquals("Penicillin", history.getAllergies().get(0).getAllergen());
        assertEquals(1, history.getRecords().size());
        assertEquals("Hypertensive Crisis Checkup", history.getRecords().get(0).getDiagnosis());
    }

    @Test
    void testUpdateClinicalRecordSuccess() {
        RecordFormDTO form = new RecordFormDTO();
        form.setPatientId(patient.getPatientId());
        form.setDoctorId(doctor.getStaffId());
        form.setDiagnosis("Initial Diagnosis");
        form.setTreatmentNotes("Initial Notes");

        Record created = medicalHistoryService.addClinicalRecord(form);
        Long recordId = created.getRecordId();

        RecordFormDTO updateForm = new RecordFormDTO();
        updateForm.setRecordId(recordId);
        updateForm.setPatientId(patient.getPatientId());
        updateForm.setDiagnosis("Updated Diagnosis - Pneumonia");
        updateForm.setTreatmentNotes("Updated Treatment Plan");
        updateForm.setBloodGroup("AB+");
        updateForm.setChronicConditions("Type II Diabetes");

        Record updated = medicalHistoryService.updateClinicalRecord(recordId, updateForm);

        assertNotNull(updated);
        assertEquals("Updated Diagnosis - Pneumonia", updated.getDiagnosis());
        assertEquals("Updated Treatment Plan", updated.getTreatmentNotes());

        Record dbRecord = recordRepository.findById(recordId).orElseThrow();
        assertEquals("Updated Diagnosis - Pneumonia", dbRecord.getDiagnosis());
    }

    @Test
    void testDeleteClinicalRecordSuccess() {
        RecordFormDTO form = new RecordFormDTO();
        form.setPatientId(patient.getPatientId());
        form.setDoctorId(doctor.getStaffId());
        form.setDiagnosis("Record To Delete");
        form.setTreatmentNotes("Notes To Delete");

        Record created = medicalHistoryService.addClinicalRecord(form);
        Long recordId = created.getRecordId();

        medicalHistoryService.deleteClinicalRecord(recordId);

        assertTrue(recordRepository.findById(recordId).isEmpty(), "Clinical record should be deleted from DB");
    }

    @Test
    void testAddAndDeleteAllergy() {
        Allergy allergy = medicalHistoryService.addPatientAllergy(patient.getPatientId(), "Dust Mites", "Mild", "Sneezing");
        assertNotNull(allergy);
        Long allergyId = allergy.getAllergyId();

        assertTrue(allergyRepository.findById(allergyId).isPresent());

        medicalHistoryService.deletePatientAllergy(allergyId);
        assertTrue(allergyRepository.findById(allergyId).isEmpty(), "Allergy should be deleted from DB");
    }
}
