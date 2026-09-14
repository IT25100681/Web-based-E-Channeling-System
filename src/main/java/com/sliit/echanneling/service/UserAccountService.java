package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.PatientRegisterDTO;
import com.sliit.echanneling.model.Patient;
import com.sliit.echanneling.model.UserAccount;

public interface UserAccountService {
    UserAccount registerPatient(PatientRegisterDTO dto);
    UserAccount findByUsername(String username);
    Patient findPatientByUsername(String username);
}
