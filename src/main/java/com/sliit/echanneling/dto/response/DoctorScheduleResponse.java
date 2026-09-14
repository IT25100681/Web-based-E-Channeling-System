package com.sliit.echanneling.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleResponse {
    private Long scheduleId;
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private String departmentName;
    private String scheduleDate;
    private String startTime;
    private String endTime;
    private Integer maxPatients;
    private long bookedCount;
    private BigDecimal consultationFee;
    private String status;
}
