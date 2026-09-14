package com.sliit.echanneling.service.impl;

import com.sliit.echanneling.model.Department;
import com.sliit.echanneling.model.Hospital;
import com.sliit.echanneling.model.Specialization;
import com.sliit.echanneling.repository.DepartmentRepository;
import com.sliit.echanneling.repository.HospitalRepository;
import com.sliit.echanneling.repository.SpecializationRepository;
import com.sliit.echanneling.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final SpecializationRepository specializationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

    @Override
    @Transactional
    public Hospital createHospital(Hospital hospital) {
        return hospitalRepository.save(hospital);
    }

    @Override
    @Transactional
    public Department createDepartment(Department department, Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found: " + hospitalId));
        department.setHospital(hospital);
        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public Specialization createSpecialization(Specialization specialization) {
        return specializationRepository.save(specialization);
    }
}
