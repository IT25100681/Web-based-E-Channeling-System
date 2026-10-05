package com.sliit.echanneling.repository;

import com.sliit.echanneling.model.HospitalManagementActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospitalManagementActivityLogRepository extends JpaRepository<HospitalManagementActivityLog, Long> {
    List<HospitalManagementActivityLog> findTop25ByOrderByCreatedAtDesc();
}
