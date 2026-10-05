package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.DepartmentForm;
import com.sliit.echanneling.dto.HospitalForm;
import com.sliit.echanneling.dto.SpecializationForm;
import com.sliit.echanneling.dto.SystemSettingForm;
import com.sliit.echanneling.model.Department;
import com.sliit.echanneling.model.Hospital;
import com.sliit.echanneling.model.HospitalManagementActivityLog;
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
    void deleteHospital(Long id);

    Department createDepartment(DepartmentForm form);
    Department updateDepartment(Long id, DepartmentForm form);
    void deleteDepartment(Long id);

    Specialization createSpecialization(SpecializationForm form);
    Specialization updateSpecialization(Long id, SpecializationForm form);
    void deleteSpecialization(Long id);

    void saveSetting(SystemSettingForm form);
    void recordBackup();
    void recordRecovery();
}
