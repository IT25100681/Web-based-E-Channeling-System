package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.response.RevenueReportDTO;

import java.util.List;

public interface ReportService {
    List<RevenueReportDTO> getRevenueReport();
    long getTotalAppointments();
    long getTotalPatients();
    long getTotalDoctors();
}
