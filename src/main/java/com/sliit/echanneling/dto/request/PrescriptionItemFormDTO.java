package com.sliit.echanneling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrescriptionItemFormDTO {
    @NotNull(message = "Medication ID is required")
    private Long medicationId;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    @NotBlank(message = "Frequency is required")
    private String frequency;

    @NotNull(message = "Duration in days is required")
    private Integer durationDays;

    private String instructions;
}
