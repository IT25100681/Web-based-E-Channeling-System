package com.sliit.echanneling.repository;

import com.sliit.echanneling.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByHospital_HospitalId(Long hospitalId);
}
