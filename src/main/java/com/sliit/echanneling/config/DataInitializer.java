package com.sliit.echanneling.config;

import com.sliit.echanneling.model.*;
import com.sliit.echanneling.model.embedded.Address;
import com.sliit.echanneling.model.enums.AppointmentStatus;
import com.sliit.echanneling.model.enums.PaymentStatus;
import com.sliit.echanneling.model.enums.PaymentType;
import com.sliit.echanneling.model.enums.Role;
import com.sliit.echanneling.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final SpecializationRepository specializationRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final MedicationRepository medicationRepository;
    private final DoctorScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Checking demo seed data for localhost environment...");

        // 1. Ensure Hospital
        Hospital hospital;
        if (hospitalRepository.count() == 0) {
            hospital = Hospital.builder()
                    .name("LankaCare General Hospital")
                    .address(new Address("123 Hospital Road", "Colombo", "00100"))
                    .contactNo("+94112345678")
                    .build();
            hospital = hospitalRepository.save(hospital);
        } else {
            hospital = hospitalRepository.findAll().get(0);
        }

        // 2. Ensure Departments
        Department cardiology;
        Department pediatrics;
        if (departmentRepository.count() == 0) {
            cardiology = departmentRepository.save(Department.builder()
                    .name("Cardiology")
                    .description("Heart and cardiovascular disease management")
                    .hospital(hospital)
                    .build());

            pediatrics = departmentRepository.save(Department.builder()
                    .name("Pediatrics")
                    .description("Child, infant, and adolescent medicine")
                    .hospital(hospital)
                    .build());

            departmentRepository.save(Department.builder()
                    .name("Neurology")
                    .description("Brain, spinal cord, and nerve treatments")
                    .hospital(hospital)
                    .build());

            departmentRepository.save(Department.builder()
                    .name("Dermatology")
                    .description("Skin, hair, and aesthetic care")
                    .hospital(hospital)
                    .build());
        } else {
            List<Department> depts = departmentRepository.findAll();
            cardiology = depts.get(0);
            pediatrics = depts.size() > 1 ? depts.get(1) : depts.get(0);
        }

        // 3. Ensure Specializations
        if (specializationRepository.count() == 0) {
            specializationRepository.save(Specialization.builder()
                    .name("Cardiologist")
                    .description("Specialist in cardiovascular diseases and heart care")
                    .build());
            specializationRepository.save(Specialization.builder()
                    .name("Pediatrician")
                    .description("Specialist in child health and growth monitoring")
                    .build());
            specializationRepository.save(Specialization.builder()
                    .name("Neurologist")
                    .description("Specialist in nervous system and brain disorders")
                    .build());
            specializationRepository.save(Specialization.builder()
                    .name("Dermatologist")
                    .description("Specialist in skin conditions and cosmetic dermatology")
                    .build());
        }

        // 4. Ensure Admin Account
        if (!userAccountRepository.existsByUsername("admin")) {
            UserAccount adminAccount = UserAccount.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("password"))
                    .role(Role.ADMIN)
                    .status("ACTIVE")
                    .build();
            userAccountRepository.save(adminAccount);
        }

        // 5. Ensure Doctors & Doctor Accounts
        Doctor doc1;
        Doctor doc2;
        if (doctorRepository.count() == 0) {
            doc1 = new Doctor();
            doc1.setName("Dr. Nadeesha Perera");
            doc1.setEmail("dr.perera@lankacare.lk");
            doc1.setPhone("+94770000002");
            doc1.setHospital(hospital);
            doc1.setDepartment(cardiology);
            doc1.setSpecialization("Cardiologist");
            doc1.setQualification("MBBS, MD (Cardiology), FRCP");
            doc1.setLicenseNo("SLMC-45892");
            doc1 = doctorRepository.save(doc1);

            if (!userAccountRepository.existsByUsername("drperera")) {
                UserAccount doc1User = UserAccount.builder()
                        .username("drperera")
                        .passwordHash(passwordEncoder.encode("password"))
                        .role(Role.DOCTOR)
                        .status("ACTIVE")
                        .staff(doc1)
                        .build();
                userAccountRepository.save(doc1User);
            }

            doc2 = new Doctor();
            doc2.setName("Dr. Malithi Fernando");
            doc2.setEmail("dr.fernando@lankacare.lk");
            doc2.setPhone("+94770000003");
            doc2.setHospital(hospital);
            doc2.setDepartment(pediatrics);
            doc2.setSpecialization("Pediatrician");
            doc2.setQualification("MBBS, DCH, MD (Pediatrics)");
            doc2.setLicenseNo("SLMC-51204");
            doc2 = doctorRepository.save(doc2);

            if (!userAccountRepository.existsByUsername("drfernando")) {
                UserAccount doc2User = UserAccount.builder()
                        .username("drfernando")
                        .passwordHash(passwordEncoder.encode("password"))
                        .role(Role.DOCTOR)
                        .status("ACTIVE")
                        .staff(doc2)
                        .build();
                userAccountRepository.save(doc2User);
            }
        } else {
            List<Doctor> docs = doctorRepository.findAll();
            doc1 = docs.get(0);
            doc2 = docs.size() > 1 ? docs.get(1) : docs.get(0);

            if (!userAccountRepository.existsByUsername("drperera")) {
                UserAccount doc1User = UserAccount.builder()
                        .username("drperera")
                        .passwordHash(passwordEncoder.encode("password"))
                        .role(Role.DOCTOR)
                        .status("ACTIVE")
                        .staff(doc1)
                        .build();
                userAccountRepository.save(doc1User);
            }
        }

        // 6. Ensure Patients & Patient Accounts
        Patient patient1;
        if (patientRepository.count() == 0) {
            patient1 = Patient.builder()
                    .name("Kamal Silva")
                    .email("kamal@gmail.com")
                    .phone("+94711112233")
                    .nic("199512345678")
                    .dob("1995-05-15")
                    .address(new Address("45 Galle Road", "Colombo", "00300"))
                    .build();
            patient1 = patientRepository.save(patient1);

            if (!userAccountRepository.existsByUsername("kamal")) {
                UserAccount patient1User = UserAccount.builder()
                        .username("kamal")
                        .passwordHash(passwordEncoder.encode("password"))
                        .role(Role.PATIENT)
                        .status("ACTIVE")
                        .patient(patient1)
                        .build();
                userAccountRepository.save(patient1User);
            }
        } else {
            patient1 = patientRepository.findAll().get(0);
            if (!userAccountRepository.existsByUsername("kamal")) {
                UserAccount patient1User = UserAccount.builder()
                        .username("kamal")
                        .passwordHash(passwordEncoder.encode("password"))
                        .role(Role.PATIENT)
                        .status("ACTIVE")
                        .patient(patient1)
                        .build();
                userAccountRepository.save(patient1User);
            }
        }

        // 7. Ensure Medications Catalog
        if (medicationRepository.count() == 0) {
            medicationRepository.save(Medication.builder().name("Paracetamol").brand("Panadol").dosageForm("Tablet").unit("500mg").build());
            medicationRepository.save(Medication.builder().name("Amoxicillin").brand("Amoxil").dosageForm("Capsule").unit("250mg").build());
            medicationRepository.save(Medication.builder().name("Atorvastatin").brand("Lipitor").dosageForm("Tablet").unit("10mg").build());
            medicationRepository.save(Medication.builder().name("Metformin").brand("Glucophage").dosageForm("Tablet").unit("500mg").build());
            medicationRepository.save(Medication.builder().name("Cetirizine").brand("Zyrtec").dosageForm("Tablet").unit("10mg").build());
            medicationRepository.save(Medication.builder().name("Ibuprofen").brand("Brufen").dosageForm("Tablet").unit("400mg").build());
        }

        // 8. Ensure Doctor Schedules
        DoctorSchedule sched1;
        if (scheduleRepository.count() == 0) {
            sched1 = DoctorSchedule.builder()
                    .doctor(doc1)
                    .department(cardiology)
                    .scheduleDate(LocalDate.now().plusDays(1).toString())
                    .startTime("09:00")
                    .endTime("12:00")
                    .maxPatients(10)
                    .consultationFee(BigDecimal.valueOf(2500.00))
                    .status("ACTIVE")
                    .build();
            sched1 = scheduleRepository.save(sched1);

            DoctorSchedule sched2 = DoctorSchedule.builder()
                    .doctor(doc2)
                    .department(pediatrics)
                    .scheduleDate(LocalDate.now().plusDays(2).toString())
                    .startTime("14:00")
                    .endTime("17:00")
                    .maxPatients(12)
                    .consultationFee(BigDecimal.valueOf(2000.00))
                    .status("ACTIVE")
                    .build();
            scheduleRepository.save(sched2);
        } else {
            sched1 = scheduleRepository.findAll().get(0);
        }

        // 9. Ensure Appointments & Payments
        Appointment app1;
        if (appointmentRepository.count() == 0) {
            app1 = Appointment.builder()
                    .patient(patient1)
                    .doctor(doc1)
                    .schedule(sched1)
                    .appointmentDate(sched1.getScheduleDate())
                    .appointmentTime("09:00")
                    .status(AppointmentStatus.CONFIRMED)
                    .referenceNo("APP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .createdAt(LocalDate.now().toString())
                    .build();
            app1 = appointmentRepository.save(app1);

            Payment pay1 = Payment.builder()
                    .appointment(app1)
                    .amount(BigDecimal.valueOf(2500.00))
                    .paymentMethod("CARD")
                    .paymentStatus(PaymentStatus.PAID)
                    .transactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .paymentType(PaymentType.PAYMENT)
                    .createdAt(LocalDate.now().toString())
                    .build();
            paymentRepository.save(pay1);
        } else {
            app1 = appointmentRepository.findAll().get(0);
        }

        // 10. Ensure Digital Prescription
        if (prescriptionRepository.count() == 0 && app1 != null) {
            List<Medication> meds = medicationRepository.findAll();
            if (!meds.isEmpty()) {
                Prescription rx = Prescription.builder()
                        .appointment(app1)
                        .doctor(doc1)
                        .patient(patient1)
                        .issueDate(LocalDate.now().toString())
                        .notes("Initial consultation complete. Prescribed rest and medication.")
                        .items(new ArrayList<>())
                        .build();

                PrescriptionItem item1 = PrescriptionItem.builder()
                        .prescription(rx)
                        .medication(meds.get(0))
                        .dosage("1 Tablet")
                        .frequency("Twice daily after food")
                        .durationDays(5)
                        .instructions("Take with warm water")
                        .build();
                rx.getItems().add(item1);

                if (meds.size() > 1) {
                    PrescriptionItem item2 = PrescriptionItem.builder()
                            .prescription(rx)
                            .medication(meds.get(1))
                            .dosage("1 Capsule")
                            .frequency("Every 8 hours")
                            .durationDays(7)
                            .instructions("Complete full course")
                            .build();
                    rx.getItems().add(item2);
                }

                prescriptionRepository.save(rx);
            }
        }

        log.info("Demo data checking complete — all localhost initial seed data is ready!");
    }
}
