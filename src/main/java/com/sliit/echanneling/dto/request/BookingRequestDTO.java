package com.sliit.echanneling.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequestDTO {
    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private String appointmentDate;
    private String appointmentTime;
    private String paymentMethod;
}
