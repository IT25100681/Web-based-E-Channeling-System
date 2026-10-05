package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.ComplaintRequestDTO;
import com.sliit.echanneling.model.Complaint;
import com.sliit.echanneling.model.ComplaintCategory;
import com.sliit.echanneling.model.Patient;
import com.sliit.echanneling.repository.ComplaintCategoryRepository;
import com.sliit.echanneling.repository.ComplaintRepository;
import com.sliit.echanneling.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ComplaintUpdateAndDeleteTest {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ComplaintCategoryRepository categoryRepository;

    private Patient patient1;
    private Patient patient2;
    private ComplaintCategory category;

    @BeforeEach
    void setUp() {
        patient1 = patientRepository.save(Patient.builder()
                .name("Test Patient 1")
                .nic("111111111V")
                .email("patient1@test.com")
                .phone("+94771111111")
                .build());

        patient2 = patientRepository.save(Patient.builder()
                .name("Test Patient 2")
                .nic("222222222V")
                .email("patient2@test.com")
                .phone("+94772222222")
                .build());

        category = categoryRepository.save(ComplaintCategory.builder()
                .name("General Service Issue")
                .description("Service related complaints")
                .build());
    }

    @Test
    void testUpdateComplaintSuccess() {
        ComplaintRequestDTO submitReq = new ComplaintRequestDTO();
        submitReq.setPatientId(patient1.getPatientId());
        submitReq.setCategoryId(category.getCategoryId());
        submitReq.setTitle("Initial Title");
        submitReq.setDescription("Initial Description");

        Complaint created = complaintService.submitComplaint(submitReq);
        Long complaintId = created.getComplaintId();

        ComplaintRequestDTO updateReq = new ComplaintRequestDTO();
        updateReq.setComplaintId(complaintId);
        updateReq.setPatientId(patient1.getPatientId());
        updateReq.setCategoryId(category.getCategoryId());
        updateReq.setTitle("Updated Title");
        updateReq.setDescription("Updated Description Details");

        Complaint updated = complaintService.updateComplaint(complaintId, updateReq, patient1.getPatientId());

        assertNotNull(updated);
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Description Details", updated.getDescription());

        Complaint dbComplaint = complaintRepository.findById(complaintId).orElseThrow();
        assertEquals("Updated Title", dbComplaint.getTitle());
        assertEquals("Updated Description Details", dbComplaint.getDescription());
    }

    @Test
    void testUpdateComplaintSecurityFailure() {
        ComplaintRequestDTO submitReq = new ComplaintRequestDTO();
        submitReq.setPatientId(patient1.getPatientId());
        submitReq.setCategoryId(category.getCategoryId());
        submitReq.setTitle("Patient 1 Title");
        submitReq.setDescription("Patient 1 Description");

        Complaint created = complaintService.submitComplaint(submitReq);
        Long complaintId = created.getComplaintId();

        ComplaintRequestDTO updateReq = new ComplaintRequestDTO();
        updateReq.setComplaintId(complaintId);
        updateReq.setPatientId(patient2.getPatientId());
        updateReq.setCategoryId(category.getCategoryId());
        updateReq.setTitle("Hacked Title");
        updateReq.setDescription("Hacked Description");

        // Patient 2 attempts to update Patient 1's complaint
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> complaintService.updateComplaint(complaintId, updateReq, patient2.getPatientId())
        );

        assertTrue(exception.getMessage().contains("You are only allowed to update your own complaints"));

        // Verify title in DB was unchanged
        Complaint dbComplaint = complaintRepository.findById(complaintId).orElseThrow();
        assertEquals("Patient 1 Title", dbComplaint.getTitle());
    }

    @Test
    void testDeleteComplaintSuccess() {
        ComplaintRequestDTO submitReq = new ComplaintRequestDTO();
        submitReq.setPatientId(patient1.getPatientId());
        submitReq.setCategoryId(category.getCategoryId());
        submitReq.setTitle("Title To Delete");
        submitReq.setDescription("Description To Delete");

        Complaint created = complaintService.submitComplaint(submitReq);
        Long complaintId = created.getComplaintId();

        complaintService.deleteComplaint(complaintId, patient1.getPatientId());

        assertTrue(complaintRepository.findById(complaintId).isEmpty(), "Complaint should be deleted from DB");
    }

    @Test
    void testDeleteComplaintSecurityFailure() {
        ComplaintRequestDTO submitReq = new ComplaintRequestDTO();
        submitReq.setPatientId(patient1.getPatientId());
        submitReq.setCategoryId(category.getCategoryId());
        submitReq.setTitle("Title Cannot Delete");
        submitReq.setDescription("Description Cannot Delete");

        Complaint created = complaintService.submitComplaint(submitReq);
        Long complaintId = created.getComplaintId();

        // Patient 2 attempts to delete Patient 1's complaint
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> complaintService.deleteComplaint(complaintId, patient2.getPatientId())
        );

        assertTrue(exception.getMessage().contains("You are only allowed to delete your own complaints"));

        // Verify complaint still exists in DB
        assertTrue(complaintRepository.findById(complaintId).isPresent(), "Complaint should still exist in DB");
    }
}
