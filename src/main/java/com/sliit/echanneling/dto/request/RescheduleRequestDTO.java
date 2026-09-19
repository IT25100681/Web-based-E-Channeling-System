package com.sliit.echanneling.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RescheduleRequestDTO {
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "New Schedule ID is required")
    private Long newScheduleId;

    private String appointmentDate;
    private String appointmentTime;
}
