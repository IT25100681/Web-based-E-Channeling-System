package com.sliit.echanneling.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class PrescriptionFormDTO {
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    private String notes;
    private List<PrescriptionItemFormDTO> items = new ArrayList<>();
}
