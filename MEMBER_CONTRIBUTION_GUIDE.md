# 👥 E-Channeling System — 6-Member GitHub Contribution & Branching Guide

This guide explains how each of the 6 group members can contribute their respective modules into the shared GitHub repository ([Web-based-E-Channeling-System](https://github.com/IT25100681/Web-based-E-Channeling-System.git)) using isolated Git feature branches and Pull Requests.

---

## 🌿 Git Branching Strategy & Workflow Rules

- **`main`**: Protected branch containing production-ready, fully integrated code.
- **`dev`**: Integration branch for combining team contributions.
- **Feature Branches**: Every member works on a designated branch named `feature/member<X>-<module-name>`.

---

## 📋 Member-by-Member Contribution Breakdown

### 🔷 Member 1: Dilahara G.R. (IT25103464) — Doctor Schedule Management
- **Branch**: `feature/member1-doctor-schedules`
- **Files to Commit**:
  - `src/main/java/com/sliit/echanneling/model/Specialization.java`
  - `src/main/java/com/sliit/echanneling/model/DoctorSchedule.java`
  - `src/main/java/com/sliit/echanneling/repository/SpecializationRepository.java`
  - `src/main/java/com/sliit/echanneling/repository/DoctorScheduleRepository.java`
  - `src/main/java/com/sliit/echanneling/dto/request/DoctorScheduleRequest.java`
  - `src/main/java/com/sliit/echanneling/dto/response/DoctorScheduleResponse.java`
  - `src/main/java/com/sliit/echanneling/service/ScheduleService.java`
  - `src/main/java/com/sliit/echanneling/service/impl/ScheduleServiceImpl.java`
  - `src/main/java/com/sliit/echanneling/controller/ScheduleController.java`
  - `src/main/resources/templates/doctor/schedule-list.html`
  - `src/main/resources/templates/doctor/schedule-form.html`

- **Git Commands**:
  ```bash
  git checkout -b feature/member1-doctor-schedules
  git add src/main/java/com/sliit/echanneling/model/Specialization.java \
          src/main/java/com/sliit/echanneling/model/DoctorSchedule.java \
          src/main/java/com/sliit/echanneling/repository/SpecializationRepository.java \
          src/main/java/com/sliit/echanneling/repository/DoctorScheduleRepository.java \
          src/main/java/com/sliit/echanneling/dto/request/DoctorScheduleRequest.java \
          src/main/java/com/sliit/echanneling/dto/response/DoctorScheduleResponse.java \
          src/main/java/com/sliit/echanneling/service/ScheduleService.java \
          src/main/java/com/sliit/echanneling/service/impl/ScheduleServiceImpl.java \
          src/main/java/com/sliit/echanneling/controller/ScheduleController.java \
          src/main/resources/templates/doctor/schedule-list.html \
          src/main/resources/templates/doctor/schedule-form.html
  git commit -m "feat(member1): Implement Doctor Schedule Management module"
  git push -u origin feature/member1-doctor-schedules
  ```

---

### 🔷 Member 2: Abeywickrama A.K.P.O.S (IT25102581) — Digital Prescription Management
- **Branch**: `feature/member2-prescriptions`
- **Files to Commit**:
  - `src/main/java/com/sliit/echanneling/model/Prescription.java`
  - `src/main/java/com/sliit/echanneling/model/PrescriptionItem.java`
  - `src/main/java/com/sliit/echanneling/model/Medication.java`
  - `src/main/java/com/sliit/echanneling/repository/PrescriptionRepository.java`
  - `src/main/java/com/sliit/echanneling/repository/PrescriptionItemRepository.java`
  - `src/main/java/com/sliit/echanneling/repository/MedicationRepository.java`
  - `src/main/java/com/sliit/echanneling/dto/request/PrescriptionFormDTO.java`
  - `src/main/java/com/sliit/echanneling/dto/request/PrescriptionItemFormDTO.java`
  - `src/main/java/com/sliit/echanneling/dto/response/PrescriptionViewDTO.java`
  - `src/main/java/com/sliit/echanneling/service/PrescriptionService.java`
  - `src/main/java/com/sliit/echanneling/service/impl/PrescriptionServiceImpl.java`
  - `src/main/java/com/sliit/echanneling/controller/PrescriptionController.java`
  - `src/main/resources/templates/prescription/list.html`
  - `src/main/resources/templates/prescription/form.html`
  - `src/main/resources/templates/prescription/view.html`

- **Git Commands**:
  ```bash
  git checkout -b feature/member2-prescriptions
  git add src/main/java/com/sliit/echanneling/model/Prescription* \
          src/main/java/com/sliit/echanneling/model/Medication.java \
          src/main/java/com/sliit/echanneling/repository/Prescription* \
          src/main/java/com/sliit/echanneling/repository/MedicationRepository.java \
          src/main/java/com/sliit/echanneling/dto/*/Prescription* \
          src/main/java/com/sliit/echanneling/service/PrescriptionService.java \
          src/main/java/com/sliit/echanneling/service/impl/PrescriptionServiceImpl.java \
          src/main/java/com/sliit/echanneling/controller/PrescriptionController.java \
          src/main/resources/templates/prescription/
  git commit -m "feat(member2): Implement Digital Prescription Management module"
  git push -u origin feature/member2-prescriptions
  ```

---

### 🔷 Member 3: Buddhasinghe B.M.K.S (IT25100681) — Appointment Management & Payments Core
- **Branch**: `feature/member3-appointment-booking`
- **Files to Commit**:
  - `src/main/java/com/sliit/echanneling/model/Appointment.java`
  - `src/main/java/com/sliit/echanneling/model/Payment.java`
  - `src/main/java/com/sliit/echanneling/repository/AppointmentRepository.java`
  - `src/main/java/com/sliit/echanneling/repository/PaymentRepository.java`
  - `src/main/java/com/sliit/echanneling/dto/request/BookingRequestDTO.java`
  - `src/main/java/com/sliit/echanneling/dto/request/PaymentRequestDTO.java`
  - `src/main/java/com/sliit/echanneling/dto/response/AppointmentViewDTO.java`
  - `src/main/java/com/sliit/echanneling/gateway/PaymentGateway.java`
  - `src/main/java/com/sliit/echanneling/gateway/impl/MockPaymentGatewayImpl.java`
  - `src/main/java/com/sliit/echanneling/service/AppointmentService.java`
  - `src/main/java/com/sliit/echanneling/service/impl/AppointmentServiceImpl.java`
  - `src/main/java/com/sliit/echanneling/service/PaymentService.java`
  - `src/main/java/com/sliit/echanneling/service/impl/PaymentServiceImpl.java`
  - `src/main/java/com/sliit/echanneling/controller/AppointmentController.java`
  - `src/main/java/com/sliit/echanneling/controller/PaymentController.java`
  - `src/main/resources/templates/patient/search-doctors.html`
  - `src/main/resources/templates/patient/book-appointment.html`
  - `src/main/resources/templates/patient/my-appointments.html`
  - `src/main/resources/templates/payment/checkout.html`
  - `src/main/resources/templates/payment/receipt.html`

- **Git Commands**:
  ```bash
  git checkout -b feature/member3-appointment-booking
  git add src/main/java/com/sliit/echanneling/model/Appointment.java \
          src/main/java/com/sliit/echanneling/model/Payment.java \
          src/main/java/com/sliit/echanneling/repository/AppointmentRepository.java \
          src/main/java/com/sliit/echanneling/repository/PaymentRepository.java \
          src/main/java/com/sliit/echanneling/gateway/ \
          src/main/java/com/sliit/echanneling/service/Appointment* \
          src/main/java/com/sliit/echanneling/service/Payment* \
          src/main/java/com/sliit/echanneling/controller/AppointmentController.java \
          src/main/java/com/sliit/echanneling/controller/PaymentController.java \
          src/main/resources/templates/patient/search-doctors.html \
          src/main/resources/templates/patient/book-appointment.html \
          src/main/resources/templates/patient/my-appointments.html \
          src/main/resources/templates/payment/
  git commit -m "feat(member3): Implement Appointment Booking & Mock Payment Gateway"
  git push -u origin feature/member3-appointment-booking
  ```

---

### 🔷 Member 4: Withanage V.T. (IT25101591) — Medical History Management
- **Branch**: `feature/member4-medical-history`
- **Files to Commit**:
  - `src/main/java/com/sliit/echanneling/model/MedicalHistory.java`
  - `src/main/java/com/sliit/echanneling/model/Record.java`
  - `src/main/java/com/sliit/echanneling/model/RecordAttachment.java`
  - `src/main/java/com/sliit/echanneling/model/Allergy.java`
  - `src/main/java/com/sliit/echanneling/repository/MedicalHistoryRepository.java`
  - `src/main/java/com/sliit/echanneling/repository/RecordRepository.java`
  - `src/main/java/com/sliit/echanneling/repository/RecordAttachmentRepository.java`
  - `src/main/java/com/sliit/echanneling/repository/AllergyRepository.java`
  - `src/main/java/com/sliit/echanneling/dto/request/RecordFormDTO.java`
  - `src/main/java/com/sliit/echanneling/dto/response/MedicalHistoryDTO.java`
  - `src/main/java/com/sliit/echanneling/util/FileStorageService.java`
  - `src/main/java/com/sliit/echanneling/service/MedicalHistoryService.java`
  - `src/main/java/com/sliit/echanneling/service/impl/MedicalHistoryServiceImpl.java`
  - `src/main/java/com/sliit/echanneling/controller/MedicalHistoryController.java`
  - `src/main/resources/templates/medical-history/patient-history.html`
  - `src/main/resources/templates/medical-history/add-record.html`

- **Git Commands**:
  ```bash
  git checkout -b feature/member4-medical-history
  git add src/main/java/com/sliit/echanneling/model/MedicalHistory.java \
          src/main/java/com/sliit/echanneling/model/Record*.java \
          src/main/java/com/sliit/echanneling/model/Allergy.java \
          src/main/java/com/sliit/echanneling/repository/MedicalHistoryRepository.java \
          src/main/java/com/sliit/echanneling/repository/Record*.java \
          src/main/java/com/sliit/echanneling/repository/AllergyRepository.java \
          src/main/java/com/sliit/echanneling/util/FileStorageService.java \
          src/main/java/com/sliit/echanneling/service/MedicalHistory* \
          src/main/java/com/sliit/echanneling/controller/MedicalHistoryController.java \
          src/main/resources/templates/medical-history/
  git commit -m "feat(member4): Implement Medical History & File Attachment module"
  git push -u origin feature/member4-medical-history
  ```

---

### 🔷 Member 5: Bandara T.M.M.M. (IT25103644) — Complaint Management & Notifications
- **Branch**: `feature/member5-complaints-notifications`
- **Files to Commit**:
  - `src/main/java/com/sliit/echanneling/model/Complaint.java`
  - `src/main/java/com/sliit/echanneling/model/ComplaintCategory.java`
  - `src/main/java/com/sliit/echanneling/model/Notification.java`
  - `src/main/java/com/sliit/echanneling/repository/ComplaintRepository.java`
  - `src/main/java/com/sliit/echanneling/repository/ComplaintCategoryRepository.java`
  - `src/main/java/com/sliit/echanneling/repository/NotificationRepository.java`
  - `src/main/java/com/sliit/echanneling/dto/request/ComplaintRequestDTO.java`
  - `src/main/java/com/sliit/echanneling/dto/response/NotificationDTO.java`
  - `src/main/java/com/sliit/echanneling/service/ComplaintService.java`
  - `src/main/java/com/sliit/echanneling/service/impl/ComplaintServiceImpl.java`
  - `src/main/java/com/sliit/echanneling/service/NotificationService.java`
  - `src/main/java/com/sliit/echanneling/service/impl/NotificationServiceImpl.java`
  - `src/main/java/com/sliit/echanneling/controller/ComplaintController.java`
  - `src/main/java/com/sliit/echanneling/controller/NotificationController.java`
  - `src/main/resources/templates/complaint/submit.html`
  - `src/main/resources/templates/complaint/list.html`

- **Git Commands**:
  ```bash
  git checkout -b feature/member5-complaints-notifications
  git add src/main/java/com/sliit/echanneling/model/Complaint*.java \
          src/main/java/com/sliit/echanneling/model/Notification.java \
          src/main/java/com/sliit/echanneling/repository/Complaint*.java \
          src/main/java/com/sliit/echanneling/repository/NotificationRepository.java \
          src/main/java/com/sliit/echanneling/service/Complaint* \
          src/main/java/com/sliit/echanneling/service/Notification* \
          src/main/java/com/sliit/echanneling/controller/ComplaintController.java \
          src/main/java/com/sliit/echanneling/controller/NotificationController.java \
          src/main/resources/templates/complaint/
  git commit -m "feat(member5): Implement Complaint Management & Notification System"
  git push -u origin feature/member5-complaints-notifications
  ```

---

### 🔷 Member 6: Lokuge D.W. (IT25101745) — Hospital & System Management, Security & Auth
- **Branch**: `feature/member6-security-hospitals-auth`
- **Files to Commit**:
  - `src/main/java/com/sliit/echanneling/model/UserAccount.java`
  - `src/main/java/com/sliit/echanneling/model/Staff.java` (Doctor, Nurse, Receptionist, Pharmacist, AdminStaff)
  - `src/main/java/com/sliit/echanneling/model/Patient.java`
  - `src/main/java/com/sliit/echanneling/model/Hospital.java`
  - `src/main/java/com/sliit/echanneling/model/Department.java`
  - `src/main/java/com/sliit/echanneling/model/SystemSetting.java`
  - `src/main/java/com/sliit/echanneling/security/`
  - `src/main/java/com/sliit/echanneling/service/UserAccountService.java`
  - `src/main/java/com/sliit/echanneling/service/HospitalService.java`
  - `src/main/java/com/sliit/echanneling/service/ReportService.java`
  - `src/main/java/com/sliit/echanneling/controller/AuthController.java`
  - `src/main/java/com/sliit/echanneling/controller/AdminController.java`
  - `src/main/java/com/sliit/echanneling/controller/PatientDashboardController.java`
  - `src/main/java/com/sliit/echanneling/controller/DoctorDashboardController.java`
  - `src/main/resources/db/migration/V1__baseline.sql`
  - `src/main/resources/db/migration/V2__seed_data.sql`
  - `src/main/resources/templates/layout/base.html`
  - `src/main/resources/templates/fragments/`
  - `src/main/resources/templates/auth/`
  - `src/main/resources/templates/admin/`

- **Git Commands**:
  ```bash
  git checkout -b feature/member6-security-hospitals-auth
  git add src/main/java/com/sliit/echanneling/security/ \
          src/main/java/com/sliit/echanneling/controller/AuthController.java \
          src/main/java/com/sliit/echanneling/controller/AdminController.java \
          src/main/resources/db/migration/ \
          src/main/resources/templates/layout/ \
          src/main/resources/templates/fragments/ \
          src/main/resources/templates/auth/ \
          src/main/resources/templates/admin/
  git commit -m "feat(member6): Implement Security, Auth, Baseline DDL & Admin Module"
  git push -u origin feature/member6-security-hospitals-auth
  ```

---

## 🚀 How to Merge All Feature Branches into Main

Once all members have pushed their feature branches, the team leader (or repository owner) can merge them into `main`:

```bash
git checkout main
git pull origin main

# Merge all feature branches
git merge feature/member6-security-hospitals-auth
git merge feature/member1-doctor-schedules
git merge feature/member2-prescriptions
git merge feature/member3-appointment-booking
git merge feature/member4-medical-history
git merge feature/member5-complaints-notifications

# Push merged codebase
git push origin main
```
