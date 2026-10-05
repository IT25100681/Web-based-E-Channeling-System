package com.sliit.echanneling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecordFormDTO {
    private Long recordId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long doctorId;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    private String treatmentNotes;
    private String bloodGroup;
    private String chronicConditions;
}
