-- ==============================================================================
-- LankaCare E-Channeling System — Re-runnable Dummy Seed Data Script
-- Database: echanneling_db (SQL Server / SSMS Compatible)
-- Default Password for all seeded users: password
-- ==============================================================================

USE echanneling_db;
GO

-- ------------------------------------------------------------------------------
-- CLEAN RESET: Clear existing records in reverse foreign-key order
-- ------------------------------------------------------------------------------
DELETE FROM notification;
DELETE FROM complaint;
DELETE FROM complaint_category;
DELETE FROM allergy;
DELETE FROM record_attachment;
DELETE FROM record;
DELETE FROM medical_history;
DELETE FROM prescription_item;
DELETE FROM prescription;
DELETE FROM medication;
DELETE FROM payment;
DELETE FROM appointment;
DELETE FROM doctor_schedule;
DELETE FROM user_account;
DELETE FROM patient;
DELETE FROM admin_staff;
DELETE FROM doctor;
DELETE FROM nurse;
DELETE FROM receptionist;
DELETE FROM pharmacist;
DELETE FROM staff;
DELETE FROM specialization;
DELETE FROM department;
DELETE FROM hospital;
DELETE FROM system_setting;
GO

-- ------------------------------------------------------------------------------
-- 1. HOSPITALS
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT hospital ON;
INSERT INTO hospital (hospital_id, name, street, city, postal_code, contact_no) VALUES
(1, 'LankaCare General Hospital', '123 Hospital Road', 'Colombo', '00100', '+94112345678'),
(2, 'LankaCare Medical Center', '45 Kandy Road', 'Kandy', '20000', '+94812234567'),
(3, 'LankaCare Healthcare Galle', '88 Matara Road', 'Galle', '80000', '+94912233445');
SET IDENTITY_INSERT hospital OFF;

-- ------------------------------------------------------------------------------
-- 2. DEPARTMENTS
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT department ON;
INSERT INTO department (department_id, name, description, hospital_id) VALUES
(1, 'Cardiology', 'Heart and cardiovascular disease management', 1),
(2, 'Pediatrics', 'Child, infant, and adolescent medicine', 1),
(3, 'Neurology', 'Brain, spinal cord, and nerve treatments', 1),
(4, 'Dermatology', 'Skin, hair, and aesthetic care', 1),
(5, 'Orthopedics', 'Bone, joint, and musculoskeletal care', 1),
(6, 'ENT (Ear, Nose, Throat)', 'Otolaryngology specialist treatments', 2),
(7, 'General Medicine', 'Comprehensive primary health consultations', 3);
SET IDENTITY_INSERT department OFF;

-- ------------------------------------------------------------------------------
-- 3. SPECIALIZATIONS
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT specialization ON;
INSERT INTO specialization (specialization_id, name, description) VALUES
(1, 'Cardiologist', 'Specialist in cardiovascular diseases and heart care'),
(2, 'Pediatrician', 'Specialist in child health and growth monitoring'),
(3, 'Neurologist', 'Specialist in nervous system and brain disorders'),
(4, 'Dermatologist', 'Specialist in skin conditions and cosmetic dermatology'),
(5, 'Orthopedic Surgeon', 'Specialist in bone fracture and joint replacement surgery'),
(6, 'ENT Specialist', 'Specialist in ear, nose, throat, and head-neck disorders'),
(7, 'General Physician', 'Specialist in general diagnosis and preventive medicine');
SET IDENTITY_INSERT specialization OFF;

-- ------------------------------------------------------------------------------
-- 4. STAFF & DOCTORS (JOINED Inheritance)
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT staff ON;
INSERT INTO staff (staff_id, name, email, phone, staff_type, hospital_id, department_id) VALUES
(1, 'System Administrator', 'admin@lankacare.lk', '+94770000001', 'ADMIN', 1, 1),
(2, 'Dr. Nadeesha Perera', 'dr.perera@lankacare.lk', '+94770000002', 'DOCTOR', 1, 1),
(3, 'Dr. Malithi Fernando', 'dr.fernando@lankacare.lk', '+94770000003', 'DOCTOR', 1, 2),
(4, 'Dr. Kasun Jayawardena', 'dr.kasun@lankacare.lk', '+94770000004', 'DOCTOR', 1, 3),
(5, 'Dr. Dilani Senanayake', 'dr.dilani@lankacare.lk', '+94770000005', 'DOCTOR', 1, 4),
(6, 'Dr. Rohan Wickramasinghe', 'dr.rohan@lankacare.lk', '+94770000006', 'DOCTOR', 2, 5),
(7, 'Nurse Anoma Silva', 'anoma@lankacare.lk', '+94770000007', 'NURSE', 1, 1),
(8, 'Receptionist Nimali', 'nimali@lankacare.lk', '+94770000008', 'RECEPTIONIST', 1, 1);
SET IDENTITY_INSERT staff OFF;

INSERT INTO admin_staff (staff_id, admin_level) VALUES (1, 'SUPER_ADMIN');

INSERT INTO doctor (staff_id, specialization, qualification, license_no) VALUES
(2, 'Cardiologist', 'MBBS, MD (Cardiology), FRCP', 'SLMC-45892'),
(3, 'Pediatrician', 'MBBS, DCH, MD (Pediatrics)', 'SLMC-51204'),
(4, 'Neurologist', 'MBBS, MD (Neurology), MRCP', 'SLMC-62319'),
(5, 'Dermatologist', 'MBBS, Diploma in Dermatology', 'SLMC-39102'),
(6, 'Orthopedic Surgeon', 'MBBS, MS (Orthopedics), FRCS', 'SLMC-48190');

INSERT INTO nurse (staff_id, ward, qualification) VALUES (7, 'Ward 3B - Cardiology', 'B.Sc. Nursing');
INSERT INTO receptionist (staff_id, desk_no) VALUES (8, 'Main Reception Desk 1');

-- ------------------------------------------------------------------------------
-- 5. PATIENTS
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT patient ON;
INSERT INTO patient (patient_id, name, email, phone, nic, dob, street, city, postal_code) VALUES
(1, 'Kamal Silva', 'kamal@gmail.com', '+94711112233', '199512345678', '1995-05-15', '45 Galle Road', 'Colombo', '00300'),
(2, 'Sunethra Perera', 'sunethra@yahoo.com', '+94722223344', '198854321098', '1988-11-20', '12 Temple Road', 'Nugegoda', '10250'),
(3, 'Ruwan Bandara', 'ruwan.b@gmail.com', '+94766667788', '200198765432', '2001-03-10', '78 Kandy Road', 'Kelaniya', '11600'),
(4, 'Priyanthi De Silva', 'priyanthi@hotmail.com', '+94755554433', '197611223344', '1976-08-25', '10 Main Street', 'Panadura', '12500');
SET IDENTITY_INSERT patient OFF;

-- ------------------------------------------------------------------------------
-- 6. USER ACCOUNTS (Single Login Table for Security)
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT user_account ON;
INSERT INTO user_account (user_id, username, password_hash, role, status, staff_id, patient_id) VALUES
(1, 'admin', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'ADMIN', 'ACTIVE', 1, NULL),
(2, 'drperera', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'DOCTOR', 'ACTIVE', 2, NULL),
(3, 'drfernando', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'DOCTOR', 'ACTIVE', 3, NULL),
(4, 'drkasun', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'DOCTOR', 'ACTIVE', 4, NULL),
(5, 'drdilani', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'DOCTOR', 'ACTIVE', 5, NULL),
(6, 'drrohan', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'DOCTOR', 'ACTIVE', 6, NULL),
(7, 'kamal', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'PATIENT', 'ACTIVE', NULL, 1),
(8, 'sunethra', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'PATIENT', 'ACTIVE', NULL, 2),
(9, 'ruwan', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'PATIENT', 'ACTIVE', NULL, 3),
(10, 'priyanthi', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'PATIENT', 'ACTIVE', NULL, 4);
SET IDENTITY_INSERT user_account OFF;

-- ------------------------------------------------------------------------------
-- 7. DOCTOR SCHEDULES
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT doctor_schedule ON;
INSERT INTO doctor_schedule (schedule_id, doctor_id, department_id, schedule_date, start_time, end_time, max_patients, consultation_fee, status) VALUES
(1, 2, 1, '2026-10-01', '09:00', '12:00', 10, 2500.00, 'ACTIVE'),
(2, 3, 2, '2026-10-01', '14:00', '17:00', 12, 2000.00, 'ACTIVE'),
(3, 4, 3, '2026-10-02', '10:00', '13:00', 8, 3000.00, 'ACTIVE'),
(4, 5, 4, '2026-10-02', '15:00', '18:00', 15, 2200.00, 'ACTIVE'),
(5, 6, 5, '2026-10-03', '09:30', '12:30', 10, 2800.00, 'ACTIVE');
SET IDENTITY_INSERT doctor_schedule OFF;

-- ------------------------------------------------------------------------------
-- 8. APPOINTMENTS
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT appointment ON;
INSERT INTO appointment (appointment_id, patient_id, doctor_id, schedule_id, appointment_date, appointment_time, status, reference_no, created_at) VALUES
(1, 1, 2, 1, '2026-10-01', '09:00', 'CONFIRMED', 'APP-8A91F2C1', '2026-09-14 10:00:00'),
(2, 2, 3, 2, '2026-10-01', '14:00', 'CONFIRMED', 'APP-7B82E3D4', '2026-09-14 11:15:00'),
(3, 3, 4, 3, '2026-10-02', '10:00', 'PENDING', 'APP-6C73D4E5', '2026-09-14 12:30:00'),
(4, 4, 5, 4, '2026-10-02', '15:00', 'COMPLETED', 'APP-5D64C5F6', '2026-09-14 09:45:00');
SET IDENTITY_INSERT appointment OFF;

-- ------------------------------------------------------------------------------
-- 9. PAYMENTS
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT payment ON;
INSERT INTO payment (payment_id, appointment_id, amount, payment_method, payment_status, transaction_ref, payment_type, created_at) VALUES
(1, 1, 2500.00, 'CARD', 'PAID', 'TXN-998811A', 'PAYMENT', '2026-09-14 10:05:00'),
(2, 2, 2000.00, 'CARD', 'PAID', 'TXN-887722B', 'PAYMENT', '2026-09-14 11:20:00'),
(3, 4, 2200.00, 'CARD', 'PAID', 'TXN-776633C', 'PAYMENT', '2026-09-14 09:50:00');
SET IDENTITY_INSERT payment OFF;

-- ------------------------------------------------------------------------------
-- 10. MEDICATIONS CATALOG
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT medication ON;
INSERT INTO medication (medication_id, name, brand, dosage_form, unit) VALUES
(1, 'Paracetamol', 'Panadol', 'Tablet', '500mg'),
(2, 'Amoxicillin', 'Amoxil', 'Capsule', '250mg'),
(3, 'Atorvastatin', 'Lipitor', 'Tablet', '10mg'),
(4, 'Metformin', 'Glucophage', 'Tablet', '500mg'),
(5, 'Omeprazole', 'Losec', 'Capsule', '20mg'),
(6, 'Cetirizine', 'Zyrtec', 'Tablet', '10mg'),
(7, 'Ibuprofen', 'Brufen', 'Tablet', '400mg');
SET IDENTITY_INSERT medication OFF;

-- ------------------------------------------------------------------------------
-- 11. PRESCRIPTIONS & PRESCRIPTION ITEMS
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT prescription ON;
INSERT INTO prescription (prescription_id, appointment_id, doctor_id, patient_id, issue_date, notes) VALUES
(1, 4, 5, 4, '2026-09-14', 'Patient presented with mild eczema rash. Prescribed antihistamines and skin ointment.');
SET IDENTITY_INSERT prescription OFF;

SET IDENTITY_INSERT prescription_item ON;
INSERT INTO prescription_item (item_id, prescription_id, medication_id, dosage, frequency, duration_days, instructions) VALUES
(1, 1, 6, '1 Tablet', 'Once daily at bedtime', 7, 'Take after food with full glass of water'),
(2, 1, 1, '1 Tablet', 'Every 8 hours as needed for pain', 3, 'Do not exceed 4 tablets in 24 hours');
SET IDENTITY_INSERT prescription_item OFF;

-- ------------------------------------------------------------------------------
-- 12. MEDICAL HISTORIES & CLINICAL RECORDS
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT medical_history ON;
INSERT INTO medical_history (history_id, patient_id, doctor_id, appointment_id, blood_group, chronic_conditions, created_at) VALUES
(1, 1, 2, 1, 'O+', 'Mild Hypertension', '2026-09-14'),
(2, 4, 5, 4, 'A+', 'Contact Dermatitis', '2026-09-14');
SET IDENTITY_INSERT medical_history OFF;

SET IDENTITY_INSERT record ON;
INSERT INTO record (record_id, history_id, doctor_id, record_date, diagnosis, treatment_notes) VALUES
(1, 1, 2, '2026-09-14', 'Essential Hypertension', 'BP recorded at 135/85 mmHg. Recommended low salt diet and regular cardio exercise.'),
(2, 2, 5, '2026-09-14', 'Acute Allergic Contact Dermatitis', 'Topical corticosteroid prescribed. Advised to avoid harsh chemical detergents.');
SET IDENTITY_INSERT record OFF;

SET IDENTITY_INSERT allergy ON;
INSERT INTO allergy (allergy_id, patient_id, allergen, severity, notes) VALUES
(1, 1, 'Penicillin', 'High', 'Causes acute urticaria and facial swelling'),
(2, 4, 'Latex', 'Moderate', 'Skin redness upon direct contact');
SET IDENTITY_INSERT allergy OFF;

-- ------------------------------------------------------------------------------
-- 13. COMPLAINT CATEGORIES & COMPLAINTS
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT complaint_category ON;
INSERT INTO complaint_category (category_id, name, description) VALUES
(1, 'Appointment Scheduling', 'Issues with booking, rescheduling, or cancelled appointments'),
(2, 'Doctor Service', 'Feedback or concerns regarding doctor consultation or behavior'),
(3, 'Payment & Billing', 'Issues with card payments, receipts, or refund requests'),
(4, 'Facility & Staff', 'Feedback regarding hospital facilities or administrative staff');
SET IDENTITY_INSERT complaint_category OFF;

SET IDENTITY_INSERT complaint ON;
INSERT INTO complaint (complaint_id, patient_id, handled_by, category_id, title, description, status, resolution_notes, created_at, updated_at) VALUES
(1, 1, 1, 1, 'Delay in Doctor Session Start Time', 'Doctor session started 20 minutes past the scheduled 09:00 AM slot.', 'RESOLVED', 'Explained emergency cardiac case delay to patient. Provided priority next visit token.', '2026-09-14 11:00:00', '2026-09-14 12:00:00'),
(2, 2, NULL, 3, 'Payment Confirmation Receipt Query', 'Did not receive instant SMS notification after card checkout.', 'SUBMITTED', NULL, '2026-09-14 11:30:00', '2026-09-14 11:30:00');
SET IDENTITY_INSERT complaint OFF;

-- ------------------------------------------------------------------------------
-- 14. NOTIFICATIONS
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT notification ON;
INSERT INTO notification (notification_id, user_id, title, message, channel, status, failure_reason, created_at) VALUES
(1, 7, 'Appointment Confirmed', 'Your appointment APP-8A91F2C1 with Dr. Nadeesha Perera on 2026-10-01 at 09:00 AM is confirmed.', 'IN_APP', 'UNREAD', NULL, '2026-09-14 10:05:00'),
(2, 8, 'Appointment Confirmed', 'Your appointment APP-7B82E3D4 with Dr. Malithi Fernando on 2026-10-01 at 02:00 PM is confirmed.', 'IN_APP', 'UNREAD', NULL, '2026-09-14 11:20:00'),
(3, 10, 'Digital Prescription Ready', 'Dr. Dilani Senanayake has issued your digital prescription for Rx #1.', 'IN_APP', 'READ', NULL, '2026-09-14 10:00:00');
SET IDENTITY_INSERT notification OFF;

-- ------------------------------------------------------------------------------
-- 15. SYSTEM SETTINGS
-- ------------------------------------------------------------------------------
SET IDENTITY_INSERT system_setting ON;
INSERT INTO system_setting (setting_id, setting_key, setting_value, description) VALUES
(1, 'SYSTEM_NAME', 'LankaCare E-Channeling System', 'Application display title'),
(2, 'CURRENCY', 'LKR', 'Default transaction currency'),
(3, 'MAX_BOOKING_ADVANCE_DAYS', '30', 'Maximum days in advance a patient can book an appointment'),
(4, 'SUPPORT_EMAIL', 'support@lankacare.lk', 'Hospital customer service contact email');
SET IDENTITY_INSERT system_setting OFF;

GO
