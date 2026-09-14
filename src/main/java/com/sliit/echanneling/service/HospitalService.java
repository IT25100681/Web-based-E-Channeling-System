package com.sliit.echanneling.service;

import com.sliit.echanneling.model.Department;
import com.sliit.echanneling.model.Hospital;
import com.sliit.echanneling.model.Specialization;

import java.util.List;

public interface HospitalService {
    List<Hospital> getAllHospitals();
    List<Department> getAllDepartments();
    List<Specialization> getAllSpecializations();
    Hospital createHospital(Hospital hospital);
    Department createDepartment(Department department, Long hospitalId);
    Specialization createSpecialization(Specialization specialization);
}
