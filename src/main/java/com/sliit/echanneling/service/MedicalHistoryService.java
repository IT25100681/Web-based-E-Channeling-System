package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.RecordFormDTO;
import com.sliit.echanneling.dto.response.MedicalHistoryDTO;
import com.sliit.echanneling.model.Allergy;
import com.sliit.echanneling.model.Record;
import org.springframework.web.multipart.MultipartFile;

public interface MedicalHistoryService {
    MedicalHistoryDTO getMedicalHistoryByPatient(Long patientId);
    Record addClinicalRecord(RecordFormDTO form);
    void addRecordAttachment(Long recordId, MultipartFile file);
    Allergy addPatientAllergy(Long patientId, String allergen, String severity, String notes);
}
