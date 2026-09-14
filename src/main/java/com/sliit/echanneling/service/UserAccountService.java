package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.DoctorRegisterDTO;
import com.sliit.echanneling.dto.request.PatientRegisterDTO;
import com.sliit.echanneling.model.Doctor;
import com.sliit.echanneling.model.Patient;
import com.sliit.echanneling.model.UserAccount;
import java.util.List;

public interface UserAccountService {
    UserAccount registerPatient(PatientRegisterDTO dto);
    UserAccount registerDoctor(DoctorRegisterDTO dto);
    UserAccount findByUsername(String username);
    Patient findPatientByUsername(String username);
    List<Doctor> getAllDoctors();
}
