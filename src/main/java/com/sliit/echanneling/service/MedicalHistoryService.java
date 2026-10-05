package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.RecordFormDTO;
import com.sliit.echanneling.dto.response.MedicalHistoryDTO;
import com.sliit.echanneling.model.Allergy;
import com.sliit.echanneling.model.Record;
import org.springframework.web.multipart.MultipartFile;

public interface MedicalHistoryService {
    MedicalHistoryDTO getMedicalHistoryByPatient(Long patientId);
    Record addClinicalRecord(RecordFormDTO form);
    Record getRecordById(Long recordId);
    Record updateClinicalRecord(Long recordId, RecordFormDTO form);
    void deleteClinicalRecord(Long recordId);
    void deleteMedicalHistory(Long historyId);
    void deleteMedicalHistoryByPatient(Long patientId);
    void addRecordAttachment(Long recordId, MultipartFile file);
    Allergy addPatientAllergy(Long patientId, String allergen, String severity, String notes);
    void deletePatientAllergy(Long allergyId);
}
