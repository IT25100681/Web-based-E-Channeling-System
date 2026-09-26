package com.sliit.echanneling;

import com.sliit.echanneling.dto.request.BookingRequestDTO;
import com.sliit.echanneling.dto.request.ComplaintRequestDTO;
import com.sliit.echanneling.dto.request.RescheduleRequestDTO;
import com.sliit.echanneling.dto.response.AppointmentViewDTO;
import com.sliit.echanneling.features.hospitalmanagement.dto.DepartmentForm;
import com.sliit.echanneling.features.hospitalmanagement.dto.HospitalForm;
import com.sliit.echanneling.features.hospitalmanagement.dto.SpecializationForm;
import com.sliit.echanneling.features.hospitalmanagement.dto.SystemSettingForm;
import com.sliit.echanneling.features.hospitalmanagement.service.HospitalManagementService;
import com.sliit.echanneling.features.hospitalmanagement.service.HospitalManagementSettings;
import com.sliit.echanneling.model.*;
import com.sliit.echanneling.model.enums.AppointmentStatus;
import com.sliit.echanneling.model.enums.ComplaintStatus;
import com.sliit.echanneling.repository.*;
import com.sliit.echanneling.service.AppointmentService;
import com.sliit.echanneling.service.ComplaintService;
import com.sliit.echanneling.service.ScheduleService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UpdateIntegrationTest {

    @Autowired
    private HospitalManagementService hospitalManagementService;

    @Autowired
    private HospitalManagementSettings settings;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SpecializationRepository specializationRepository;

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    void testHospitalUpdate() {
        HospitalForm createForm = new HospitalForm();
        createForm.setCode("HOSP-TEST-1");
        createForm.setName("Test Hospital Initial");
        createForm.setContactNo("+94112223333");
        createForm.setStreet("123 Test St");
        createForm.setCity("Colombo");
        createForm.setPostalCode("00100");
        createForm.setActive(true);

        Hospital created = hospitalManagementService.createHospital(createForm);
        Long id = created.getHospitalId();

        HospitalForm updateForm = new HospitalForm();
        updateForm.setCode("HOSP-TEST-1");
        updateForm.setName("Test Hospital Updated Name");
        updateForm.setContactNo("+94119998888");
        updateForm.setStreet("456 Updated St");
        updateForm.setCity("Kandy");
        updateForm.setPostalCode("20000");
        updateForm.setActive(true);

        hospitalManagementService.updateHospital(id, updateForm);

        Hospital updated = hospitalRepository.findById(id).orElseThrow();
        assertEquals("Test Hospital Updated Name", updated.getName());
        assertEquals("+94119998888", updated.getContactNo());
        assertEquals("Kandy", updated.getAddress().getCity());
    }

    @Test
    void testHospitalDeactivateAndRestore() {
        HospitalForm createForm = new HospitalForm();
        createForm.setCode("HOSP-TEST-2");
        createForm.setName("Test Hospital Deactivate");
        createForm.setActive(true);

        Hospital created = hospitalManagementService.createHospital(createForm);
        Long id = created.getHospitalId();

        hospitalManagementService.deactivateHospital(id);
        Hospital deactivated = hospitalRepository.findById(id).orElseThrow();
        assertFalse(deactivated.getActive(), "Hospital should be deactivated in DB");

        hospitalManagementService.restoreHospital(id);
        Hospital restored = hospitalRepository.findById(id).orElseThrow();
        assertTrue(restored.getActive(), "Hospital should be active after restore in DB");
    }

    @Test
    void testDepartmentUpdate() {
        Hospital hospital = hospitalRepository.findAll().get(0);

        DepartmentForm createForm = new DepartmentForm();
        createForm.setCode("DEPT-TEST-1");
        createForm.setName("Test Department Initial");
        createForm.setHospitalId(hospital.getHospitalId());
        createForm.setActive(true);

        Department created = hospitalManagementService.createDepartment(createForm);
        Long id = created.getDepartmentId();

        DepartmentForm updateForm = new DepartmentForm();
        updateForm.setCode("DEPT-TEST-1");
        updateForm.setName("Test Department Updated");
        updateForm.setHospitalId(hospital.getHospitalId());
        updateForm.setDescription("Updated Description");
        updateForm.setActive(true);

        hospitalManagementService.updateDepartment(id, updateForm);

        Department updated = departmentRepository.findById(id).orElseThrow();
        assertEquals("Test Department Updated", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
    }

    @Test
    void testDepartmentDeactivateAndRestore() {
        Hospital hospital = hospitalRepository.findAll().get(0);

        DepartmentForm createForm = new DepartmentForm();
        createForm.setCode("DEPT-TEST-2");
        createForm.setName("Test Department Deactivate");
        createForm.setHospitalId(hospital.getHospitalId());
        createForm.setActive(true);

        Department created = hospitalManagementService.createDepartment(createForm);
        Long id = created.getDepartmentId();

        hospitalManagementService.deactivateDepartment(id);
        Department deactivated = departmentRepository.findById(id).orElseThrow();
        assertFalse(deactivated.getActive(), "Department should be deactivated in DB");

        hospitalManagementService.restoreDepartment(id);
        Department restored = departmentRepository.findById(id).orElseThrow();
        assertTrue(restored.getActive(), "Department should be active after restore in DB");
    }

    @Test
    void testSpecializationUpdate() {
        SpecializationForm createForm = new SpecializationForm();
        createForm.setCode("SPEC-TEST-1");
        createForm.setName("Test Spec Initial");
        createForm.setActive(true);

        Specialization created = hospitalManagementService.createSpecialization(createForm);
        Long id = created.getSpecializationId();

        SpecializationForm updateForm = new SpecializationForm();
        updateForm.setCode("SPEC-TEST-1");
        updateForm.setName("Test Spec Updated");
        updateForm.setDescription("New description");
        updateForm.setActive(true);

        hospitalManagementService.updateSpecialization(id, updateForm);

        Specialization updated = specializationRepository.findById(id).orElseThrow();
        assertEquals("Test Spec Updated", updated.getName());
        assertEquals("New description", updated.getDescription());
    }

    @Test
    void testSpecializationDeactivateAndRestore() {
        SpecializationForm createForm = new SpecializationForm();
        createForm.setCode("SPEC-TEST-2");
        createForm.setName("Test Spec Deactivate");
        createForm.setActive(true);

        Specialization created = hospitalManagementService.createSpecialization(createForm);
        Long id = created.getSpecializationId();

        hospitalManagementService.deactivateSpecialization(id);
        Specialization deactivated = specializationRepository.findById(id).orElseThrow();
        assertFalse(deactivated.getActive(), "Specialization should be deactivated in DB");

        hospitalManagementService.restoreSpecialization(id);
        Specialization restored = specializationRepository.findById(id).orElseThrow();
        assertTrue(restored.getActive(), "Specialization should be active after restore in DB");
    }

    @Test
    void testSystemSettingUpsert() {
        SystemSettingForm form = new SystemSettingForm();
        form.setSettingKey("test.setting.key");
        form.setSettingValue("initial_val");
        form.setDescription("initial desc");

        settings.upsert(form);

        SystemSetting setting = systemSettingRepository.findBySettingKey("test.setting.key").orElseThrow();
        assertEquals("initial_val", setting.getSettingValue());

        form.setSettingValue("updated_val");
        settings.upsert(form);

        SystemSetting updatedSetting = systemSettingRepository.findBySettingKey("test.setting.key").orElseThrow();
        assertEquals("updated_val", updatedSetting.getSettingValue());
    }

    @Autowired
    private ComplaintCategoryRepository categoryRepository;

    @Test
    void testComplaintStatusUpdate() {
        Patient patient = patientRepository.findAll().get(0);

        ComplaintCategory category = categoryRepository.findAll().stream().findFirst().orElseGet(() ->
                categoryRepository.save(ComplaintCategory.builder()
                        .name("Service Issue")
                        .description("Test Category")
                        .build()));

        Complaint complaint = complaintRepository.save(Complaint.builder()
                .patient(patient)
                .category(category)
                .title("Test Complaint")
                .description("Test Description")
                .status(ComplaintStatus.SUBMITTED)
                .createdAt("2026-09-26 10:00:00")
                .build());

        Long complaintId = complaint.getComplaintId();

        complaintService.updateComplaintStatus(complaintId, ComplaintStatus.IN_PROGRESS, null, "Working on it");

        Complaint updated = complaintRepository.findById(complaintId).orElseThrow();
        assertEquals(ComplaintStatus.IN_PROGRESS, updated.getStatus());
        assertEquals("Working on it", updated.getResolutionNotes());
    }

    @Test
    void testScheduleStatusUpdate() {
        DoctorSchedule schedule = scheduleRepository.findAll().get(0);
        Long scheduleId = schedule.getScheduleId();
        String originalStatus = schedule.getStatus();

        String newStatus = "ACTIVE".equals(originalStatus) ? "CANCELLED" : "ACTIVE";
        scheduleService.updateScheduleStatus(scheduleId, newStatus);

        DoctorSchedule updated = scheduleRepository.findById(scheduleId).orElseThrow();
        assertEquals(newStatus, updated.getStatus());
    }

    @Test
    void testRescheduleAppointment() {
        Appointment existingApp = appointmentRepository.findAll().get(0);
        Long appLongId = existingApp.getAppointmentId();
        DoctorSchedule schedule = existingApp.getSchedule();

        // 1. Reschedule to same schedule and same requested time (09:00) - should retain 09:00 without self-conflict
        RescheduleRequestDTO rescheduleSame = new RescheduleRequestDTO();
        rescheduleSame.setAppointmentId(appLongId);
        rescheduleSame.setNewScheduleId(schedule.getScheduleId());
        rescheduleSame.setAppointmentDate(existingApp.getAppointmentDate());
        rescheduleSame.setAppointmentTime(existingApp.getAppointmentTime());

        AppointmentViewDTO rescheduledSame = appointmentService.rescheduleAppointment(appLongId, rescheduleSame, "testuser");
        assertNotNull(rescheduledSame);
        assertEquals(existingApp.getAppointmentTime(), rescheduledSame.getAppointmentTime());

        // 2. Reschedule to a new date and time
        RescheduleRequestDTO rescheduleNew = new RescheduleRequestDTO();
        rescheduleNew.setAppointmentId(appLongId);
        rescheduleNew.setNewScheduleId(schedule.getScheduleId());
        rescheduleNew.setAppointmentDate("2026-10-10");
        rescheduleNew.setAppointmentTime("10:00");

        AppointmentViewDTO rescheduledNew = appointmentService.rescheduleAppointment(appLongId, rescheduleNew, "testuser");
        assertNotNull(rescheduledNew);

        Appointment updatedDbApp = appointmentRepository.findById(appLongId).orElseThrow();
        assertEquals("2026-10-10", updatedDbApp.getAppointmentDate());
        assertEquals("10:00", updatedDbApp.getAppointmentTime());
    }
}
