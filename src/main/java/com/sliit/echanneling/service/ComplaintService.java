package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.ComplaintRequestDTO;
import com.sliit.echanneling.model.Complaint;
import com.sliit.echanneling.model.ComplaintCategory;
import com.sliit.echanneling.model.enums.ComplaintStatus;

import java.util.List;

public interface ComplaintService {
    Complaint submitComplaint(ComplaintRequestDTO request);
    List<Complaint> getComplaintsByPatient(Long patientId);
    List<Complaint> getAllComplaints();
    List<ComplaintCategory> getAllCategories();
    Complaint updateComplaintStatus(Long complaintId, ComplaintStatus status, Long staffId, String resolutionNotes);
}
