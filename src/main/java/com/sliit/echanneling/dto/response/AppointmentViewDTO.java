package com.sliit.echanneling.dto.response;

import com.sliit.echanneling.model.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentViewDTO {
    private Long appointmentId;
    private String referenceNo;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private String appointmentDate;
    private String appointmentTime;
    private AppointmentStatus status;
    private BigDecimal fee;
    private String paymentStatus;
    private String createdAt;
}
