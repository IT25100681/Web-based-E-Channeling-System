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
public class RevenueReportDTO {
    private String doctorName;
    private String departmentName;
    private long totalAppointments;
    private BigDecimal totalRevenue;
}
