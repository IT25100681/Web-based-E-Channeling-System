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
public class MedicalHistoryDTO {
    private Long historyId;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private String bloodGroup;
    private String chronicConditions;
    private List<AllergyView> allergies;
    private List<RecordView> records;
    private List<PrescriptionView> prescriptions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllergyView {
        private Long allergyId;
        private String allergen;
        private String severity;
        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordView {
        private Long recordId;
        private String recordDate;
        private String doctorName;
        private String diagnosis;
        private String treatmentNotes;
        private List<AttachmentView> attachments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentView {
        private Long attachmentId;
        private String fileName;
        private String fileType;
        private String filePath;
        private String uploadDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionView {
        private Long prescriptionId;
        private String issueDate;
        private String doctorName;
        private String notes;
        private List<PrescriptionItemView> items;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionItemView {
        private String medicineName;
        private String dosage;
        private String frequency;
        private Integer durationDays;
        private String instructions;
    }
}
