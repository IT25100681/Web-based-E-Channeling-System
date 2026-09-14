package com.sliit.echanneling.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionViewDTO {
    private Long prescriptionId;
    private Long appointmentId;
    private String referenceNo;
    private String doctorName;
    private String specialization;
    private String patientName;
    private String issueDate;
    private String notes;
    private List<ItemView> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemView {
        private String medicationName;
        private String brand;
        private String dosage;
        private String frequency;
        private Integer durationDays;
        private String instructions;
    }
}
