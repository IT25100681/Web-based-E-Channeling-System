package com.sliit.echanneling.repository;

import com.sliit.echanneling.model.Complaint;
import com.sliit.echanneling.model.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByPatient_PatientId(Long patientId);
    List<Complaint> findByStatus(ComplaintStatus status);
    List<Complaint> findByHandledBy_StaffId(Long staffId);
}
