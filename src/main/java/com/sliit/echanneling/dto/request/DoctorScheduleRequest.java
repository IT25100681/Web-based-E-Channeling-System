package com.sliit.echanneling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DoctorScheduleRequest {
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    private Long departmentId;

    @NotBlank(message = "Schedule date is required")
    private String scheduleDate;

    @NotBlank(message = "Start time is required")
    private String startTime;

    @NotBlank(message = "End time is required")
    private String endTime;

    @NotNull(message = "Max patients limit is required")
    private Integer maxPatients;

    @NotNull(message = "Consultation fee is required")
    private BigDecimal consultationFee;
}
