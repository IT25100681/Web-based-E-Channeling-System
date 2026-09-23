package com.sliit.echanneling.features.hospitalmanagement.service;

import com.sliit.echanneling.features.hospitalmanagement.dto.DepartmentForm;
import com.sliit.echanneling.features.hospitalmanagement.dto.HospitalForm;
import com.sliit.echanneling.features.hospitalmanagement.dto.SpecializationForm;
import com.sliit.echanneling.features.hospitalmanagement.dto.SystemSettingForm;
import com.sliit.echanneling.features.hospitalmanagement.model.HospitalManagementActivityLog;
import com.sliit.echanneling.model.Department;
import com.sliit.echanneling.model.Hospital;
import com.sliit.echanneling.model.Specialization;
import com.sliit.echanneling.model.SystemSetting;

import java.util.List;

public interface HospitalManagementService {
    List<Hospital> getHospitals(String query);
    List<Department> getDepartments(String query);
    List<Specialization> getSpecializations(String query);
    List<HospitalManagementActivityLog> getRecentLogs();
    List<SystemSetting> getSettings();

    Hospital createHospital(HospitalForm form);
    Hospital updateHospital(Long id, HospitalForm form);
    void deactivateHospital(Long id);
    void restoreHospital(Long id);
    void deleteHospital(Long id);

    Department createDepartment(DepartmentForm form);
    Department updateDepartment(Long id, DepartmentForm form);
    void deactivateDepartment(Long id);
    void restoreDepartment(Long id);
    void deleteDepartment(Long id);

    Specialization createSpecialization(SpecializationForm form);
    Specialization updateSpecialization(Long id, SpecializationForm form);
    void deactivateSpecialization(Long id);
    void restoreSpecialization(Long id);
    void deleteSpecialization(Long id);

    void saveSetting(SystemSettingForm form);
    void recordBackup();
    void recordRecovery();
}
