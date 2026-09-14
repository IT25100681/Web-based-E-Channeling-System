package com.sliit.echanneling.service.impl;

import com.sliit.echanneling.dto.request.PatientRegisterDTO;
import com.sliit.echanneling.model.Patient;
import com.sliit.echanneling.model.UserAccount;
import com.sliit.echanneling.model.embedded.Address;
import com.sliit.echanneling.model.enums.Role;
import com.sliit.echanneling.repository.PatientRepository;
import com.sliit.echanneling.repository.UserAccountRepository;
import com.sliit.echanneling.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PatientRepository patientRepository;
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
}
