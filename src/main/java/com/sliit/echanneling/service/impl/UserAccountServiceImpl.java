package com.sliit.echanneling.service.impl;

import com.sliit.echanneling.dto.request.DoctorRegisterDTO;
import com.sliit.echanneling.dto.request.PatientRegisterDTO;
import com.sliit.echanneling.model.Department;
import com.sliit.echanneling.model.Doctor;
import com.sliit.echanneling.model.Hospital;
import com.sliit.echanneling.model.Patient;
import com.sliit.echanneling.model.UserAccount;
import com.sliit.echanneling.model.embedded.Address;
import com.sliit.echanneling.model.enums.Role;
import com.sliit.echanneling.repository.DepartmentRepository;
import com.sliit.echanneling.repository.DoctorRepository;
import com.sliit.echanneling.repository.HospitalRepository;
import com.sliit.echanneling.repository.PatientRepository;
import com.sliit.echanneling.repository.UserAccountRepository;
import com.sliit.echanneling.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserAccount registerPatient(PatientRegisterDTO dto) {
        if (userAccountRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists!");
        }

        Patient patient = Patient.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .nic(dto.getNic())
                .dob(dto.getDob())
                .address(new Address(dto.getStreet(), dto.getCity(), dto.getPostalCode()))
                .build();
        patient = patientRepository.save(patient);

        UserAccount userAccount = UserAccount.builder()
                .username(dto.getUsername())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(Role.PATIENT)
                .status("ACTIVE")
                .patient(patient)
                .build();

        return userAccountRepository.save(userAccount);
    }

    @Override
    @Transactional
    public UserAccount registerDoctor(DoctorRegisterDTO dto) {
        if (userAccountRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists!");
        }

        Hospital hospital = hospitalRepository.findById(dto.getHospitalId())
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found: " + dto.getHospitalId()));

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + dto.getDepartmentId()));

        Doctor doctor = new Doctor();
        doctor.setName(dto.getName());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setHospital(hospital);
        doctor.setDepartment(department);
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setQualification(dto.getQualification());
        doctor.setLicenseNo(dto.getLicenseNo());
        doctor = doctorRepository.save(doctor);

        UserAccount userAccount = UserAccount.builder()
                .username(dto.getUsername())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(Role.DOCTOR)
                .status("ACTIVE")
                .staff(doctor)
                .build();

        return userAccountRepository.save(userAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccount findByUsername(String username) {
        return userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public Patient findPatientByUsername(String username) {
        UserAccount account = findByUsername(username);
        if (account.getPatient() == null) {
            throw new IllegalStateException("User account does not have an associated patient profile");
        }
        return account.getPatient();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
}
