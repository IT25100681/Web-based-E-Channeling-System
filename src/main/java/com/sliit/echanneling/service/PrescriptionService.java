package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.PrescriptionFormDTO;
import com.sliit.echanneling.dto.response.PrescriptionViewDTO;
import com.sliit.echanneling.model.Medication;
import com.sliit.echanneling.model.Prescription;

import java.util.List;

public interface PrescriptionService {
    Prescription createPrescription(PrescriptionFormDTO form);
    PrescriptionViewDTO getPrescriptionById(Long prescriptionId);
    PrescriptionViewDTO getPrescriptionByAppointment(Long appointmentId);
    List<PrescriptionViewDTO> getPrescriptionsByPatient(Long patientId);
    List<PrescriptionViewDTO> getPrescriptionsByDoctor(Long doctorId);
    List<PrescriptionViewDTO> getAllPrescriptions();
    List<Medication> getAllMedications();
}
