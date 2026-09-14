package com.sliit.echanneling.service.impl;

import com.sliit.echanneling.dto.request.ComplaintRequestDTO;
import com.sliit.echanneling.model.*;
import com.sliit.echanneling.model.enums.ComplaintStatus;
import com.sliit.echanneling.repository.*;
import com.sliit.echanneling.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintCategoryRepository categoryRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;

    @Override
    @Transactional
    public Complaint submitComplaint(ComplaintRequestDTO request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + request.getPatientId()));

        ComplaintCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + request.getCategoryId()));

        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Complaint complaint = Complaint.builder()
                .patient(patient)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(ComplaintStatus.SUBMITTED)
                .createdAt(nowStr)
                .updatedAt(nowStr)
                .build();

        return complaintRepository.save(complaint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Complaint> getComplaintsByPatient(Long patientId) {
        return complaintRepository.findByPatient_PatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Complaint updateComplaintStatus(Long complaintId, ComplaintStatus status, Long staffId, String resolutionNotes) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found: " + complaintId));

        if (staffId != null) {
            Staff staff = staffRepository.findById(staffId).orElse(null);
            complaint.setHandledBy(staff);
        }

        complaint.setStatus(status);
        if (resolutionNotes != null && !resolutionNotes.isBlank()) {
            complaint.setResolutionNotes(resolutionNotes);
        }
        complaint.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return complaintRepository.save(complaint);
    }
}
