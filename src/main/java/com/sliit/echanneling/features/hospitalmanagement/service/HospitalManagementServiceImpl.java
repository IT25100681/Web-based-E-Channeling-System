package com.sliit.echanneling.features.hospitalmanagement.service;

import com.sliit.echanneling.features.hospitalmanagement.dto.DepartmentForm;
import com.sliit.echanneling.features.hospitalmanagement.dto.HospitalForm;
import com.sliit.echanneling.features.hospitalmanagement.dto.SpecializationForm;
import com.sliit.echanneling.features.hospitalmanagement.dto.SystemSettingForm;
import com.sliit.echanneling.features.hospitalmanagement.exception.HospitalManagementException;
import com.sliit.echanneling.features.hospitalmanagement.model.HospitalManagementActivityLog;
import com.sliit.echanneling.features.hospitalmanagement.repository.HospitalManagementActivityLogRepository;
import com.sliit.echanneling.features.hospitalmanagement.repository.HospitalManagementDepartmentRepository;
import com.sliit.echanneling.features.hospitalmanagement.repository.HospitalManagementHospitalRepository;
import com.sliit.echanneling.features.hospitalmanagement.repository.HospitalManagementSpecializationRepository;
import com.sliit.echanneling.features.hospitalmanagement.util.InputSanitizer;
import com.sliit.echanneling.model.Department;
import com.sliit.echanneling.model.Hospital;
import com.sliit.echanneling.model.Specialization;
import com.sliit.echanneling.model.SystemSetting;
import com.sliit.echanneling.model.embedded.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalManagementServiceImpl implements HospitalManagementService {

    private final HospitalManagementHospitalRepository hospitalRepository;
    private final HospitalManagementDepartmentRepository departmentRepository;
    private final HospitalManagementSpecializationRepository specializationRepository;
    private final HospitalManagementActivityLogRepository activityLogRepository;
    private final HospitalManagementSettings settings;
    private final ActivityLogFactory activityLogFactory;
    private final InputSanitizer sanitizer;

    @Override
    @Transactional(readOnly = true)
    public List<Hospital> getHospitals(String query) {
        return hasSearch(query) ? hospitalRepository.search(sanitizer.clean(query)) : hospitalRepository.findAllByOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getDepartments(String query) {
        return hasSearch(query) ? departmentRepository.search(sanitizer.clean(query)) : departmentRepository.findAllWithHospital();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Specialization> getSpecializations(String query) {
        return hasSearch(query) ? specializationRepository.search(sanitizer.clean(query)) : specializationRepository.findAllByOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HospitalManagementActivityLog> getRecentLogs() {
        return activityLogRepository.findTop25ByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemSetting> getSettings() {
        return settings.getAllSettings();
    }

    @Override
    @Transactional
    public Hospital createHospital(HospitalForm form) {
        requireUniqueHospitalCode(form.getCode(), null);
        Hospital hospital = new Hospital();
        applyHospitalForm(hospital, form);
        Hospital saved = hospitalRepository.save(hospital);
        log("HOSPITAL", saved.getHospitalId(), "CREATE", "Created hospital " + saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public Hospital updateHospital(Long id, HospitalForm form) {
        requireUniqueHospitalCode(form.getCode(), id);
        Hospital hospital = findHospital(id);
        applyHospitalForm(hospital, form);
        Hospital saved = hospitalRepository.save(hospital);
        log("HOSPITAL", saved.getHospitalId(), "UPDATE", "Updated hospital " + saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public void deactivateHospital(Long id) {
        Hospital hospital = findHospital(id);
        hospital.setActive(false);
        hospitalRepository.save(hospital);
        log("HOSPITAL", id, "DEACTIVATE", "Deactivated hospital " + hospital.getName());
    }

    @Override
    @Transactional
    public void restoreHospital(Long id) {
        Hospital hospital = findHospital(id);
        hospital.setActive(true);
        hospitalRepository.save(hospital);
        log("HOSPITAL", id, "RESTORE", "Restored hospital " + hospital.getName());
    }

    @Override
    @Transactional
    public void deleteHospital(Long id) {
        Hospital hospital = findHospital(id);
        hospital.setActive(false);
        hospitalRepository.save(hospital);
        log("HOSPITAL", id, "SAFE_DEACTIVATE", "Hospital was safely deactivated instead of hard-deleted.");
    }

    @Override
    @Transactional
    public Department createDepartment(DepartmentForm form) {
        requireUniqueDepartmentCode(form.getCode(), null);
        Department department = new Department();
        applyDepartmentForm(department, form);
        Department saved = departmentRepository.save(department);
        log("DEPARTMENT", saved.getDepartmentId(), "CREATE", "Created department " + saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public Department updateDepartment(Long id, DepartmentForm form) {
        requireUniqueDepartmentCode(form.getCode(), id);
        Department department = findDepartment(id);
        applyDepartmentForm(department, form);
        Department saved = departmentRepository.save(department);
        log("DEPARTMENT", saved.getDepartmentId(), "UPDATE", "Updated department " + saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public void deactivateDepartment(Long id) {
        Department department = findDepartment(id);
        department.setActive(false);
        departmentRepository.save(department);
        log("DEPARTMENT", id, "DEACTIVATE", "Deactivated department " + department.getName());
    }

    @Override
    @Transactional
    public void restoreDepartment(Long id) {
        Department department = findDepartment(id);
        department.setActive(true);
        departmentRepository.save(department);
        log("DEPARTMENT", id, "RESTORE", "Restored department " + department.getName());
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = findDepartment(id);
        department.setActive(false);
        departmentRepository.save(department);
        log("DEPARTMENT", id, "SAFE_DEACTIVATE", "Department was safely deactivated instead of hard-deleted.");
    }

    @Override
    @Transactional
    public Specialization createSpecialization(SpecializationForm form) {
        requireUniqueSpecialization(form.getCode(), form.getName(), null);
        Specialization specialization = new Specialization();
        applySpecializationForm(specialization, form);
        Specialization saved = specializationRepository.save(specialization);
        log("SPECIALIZATION", saved.getSpecializationId(), "CREATE", "Created specialization " + saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public Specialization updateSpecialization(Long id, SpecializationForm form) {
        requireUniqueSpecialization(form.getCode(), form.getName(), id);
        Specialization specialization = findSpecialization(id);
        applySpecializationForm(specialization, form);
        Specialization saved = specializationRepository.save(specialization);
        log("SPECIALIZATION", saved.getSpecializationId(), "UPDATE", "Updated specialization " + saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public void deactivateSpecialization(Long id) {
        Specialization specialization = findSpecialization(id);
        specialization.setActive(false);
        specializationRepository.save(specialization);
        log("SPECIALIZATION", id, "DEACTIVATE", "Deactivated specialization " + specialization.getName());
    }

    @Override
    @Transactional
    public void restoreSpecialization(Long id) {
        Specialization specialization = findSpecialization(id);
        specialization.setActive(true);
        specializationRepository.save(specialization);
        log("SPECIALIZATION", id, "RESTORE", "Restored specialization " + specialization.getName());
    }

    @Override
    @Transactional
    public void deleteSpecialization(Long id) {
        Specialization specialization = findSpecialization(id);
        specialization.setActive(false);
        specializationRepository.save(specialization);
        log("SPECIALIZATION", id, "SAFE_DEACTIVATE", "Specialization was safely deactivated instead of hard-deleted.");
    }

    @Override
    @Transactional
    public void saveSetting(SystemSettingForm form) {
        SystemSetting setting = settings.upsert(form);
        log("SETTING", setting.getSettingId(), "UPSERT", "Saved setting " + setting.getSettingKey());
    }

    @Override
    @Transactional
    public void recordBackup() {
        String value = "Recorded at " + LocalDateTime.now();
        settings.markBackup(value);
        log("SYSTEM", null, "BACKUP_RECORDED", "Recorded backup checkpoint: " + value);
    }

    @Override
    @Transactional
    public void recordRecovery() {
        String value = "Recorded at " + LocalDateTime.now();
        settings.markRecovery(value);
        log("SYSTEM", null, "RECOVERY_RECORDED", "Recorded recovery checkpoint: " + value);
    }

    private void applyHospitalForm(Hospital hospital, HospitalForm form) {
        hospital.setCode(sanitizer.clean(form.getCode()).toUpperCase());
        hospital.setName(sanitizer.clean(form.getName()));
        hospital.setContactNo(sanitizer.cleanNullable(form.getContactNo()));
        hospital.setAddress(new Address(
                sanitizer.cleanNullable(form.getStreet()),
                sanitizer.cleanNullable(form.getCity()),
                sanitizer.cleanNullable(form.getPostalCode())
        ));
        hospital.setActive(Boolean.TRUE.equals(form.getActive()));
    }

    private void applyDepartmentForm(Department department, DepartmentForm form) {
        department.setCode(sanitizer.clean(form.getCode()).toUpperCase());
        department.setName(sanitizer.clean(form.getName()));
        department.setDescription(sanitizer.cleanNullable(form.getDescription()));
        department.setHospital(findHospital(form.getHospitalId()));
        department.setActive(Boolean.TRUE.equals(form.getActive()));
    }

    private void applySpecializationForm(Specialization specialization, SpecializationForm form) {
        specialization.setCode(sanitizer.clean(form.getCode()).toUpperCase());
        specialization.setName(sanitizer.clean(form.getName()));
        specialization.setDescription(sanitizer.cleanNullable(form.getDescription()));
        specialization.setActive(Boolean.TRUE.equals(form.getActive()));
    }

    private Hospital findHospital(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new HospitalManagementException("Hospital not found."));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new HospitalManagementException("Department not found."));
    }

    private Specialization findSpecialization(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new HospitalManagementException("Specialization not found."));
    }

    private void requireUniqueHospitalCode(String code, Long currentId) {
        boolean exists = currentId == null
                ? hospitalRepository.existsByCodeIgnoreCase(code)
                : hospitalRepository.existsByCodeIgnoreCaseAndHospitalIdNot(code, currentId);
        if (exists) {
            throw new HospitalManagementException("Hospital code already exists.");
        }
    }

    private void requireUniqueDepartmentCode(String code, Long currentId) {
        boolean exists = currentId == null
                ? departmentRepository.existsByCodeIgnoreCase(code)
                : departmentRepository.existsByCodeIgnoreCaseAndDepartmentIdNot(code, currentId);
        if (exists) {
            throw new HospitalManagementException("Department code already exists.");
        }
    }

    private void requireUniqueSpecialization(String code, String name, Long currentId) {
        boolean codeExists = currentId == null
                ? specializationRepository.existsByCodeIgnoreCase(code)
                : specializationRepository.existsByCodeIgnoreCaseAndSpecializationIdNot(code, currentId);
        boolean nameExists = currentId == null
                ? specializationRepository.existsByNameIgnoreCase(name)
                : specializationRepository.existsByNameIgnoreCaseAndSpecializationIdNot(name, currentId);
        if (codeExists) {
            throw new HospitalManagementException("Specialization code already exists.");
        }
        if (nameExists) {
            throw new HospitalManagementException("Specialization name already exists.");
        }
    }

    private boolean hasSearch(String query) {
        return query != null && !query.trim().isBlank();
    }

    private void log(String entityType, Long entityId, String action, String message) {
        activityLogRepository.save(activityLogFactory.create(entityType, entityId, action, message));
    }
}
