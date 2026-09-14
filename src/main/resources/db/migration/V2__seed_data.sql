-- V2__seed_data.sql: Baseline Reference Data & Initial Accounts (SQL Server Syntax)

-- 1. Insert Hospital
SET IDENTITY_INSERT hospital ON;
INSERT INTO hospital (hospital_id, name, street, city, postal_code, contact_no) 
VALUES (1, 'LankaCare General Hospital', '123 Hospital Road', 'Colombo', '00100', '+94112345678');
SET IDENTITY_INSERT hospital OFF;

-- 2. Insert Departments
SET IDENTITY_INSERT department ON;
INSERT INTO department (department_id, name, description, hospital_id) VALUES 
(1, 'Cardiology', 'Heart and cardiovascular system care', 1),
(2, 'Pediatrics', 'Infant, child, and adolescent medicine', 1),
(3, 'Neurology', 'Brain, nerve, and spinal cord treatment', 1),
(4, 'Dermatology', 'Skin, hair, and nail health care', 1),
(5, 'Orthopedics', 'Bones, joints, and muscular health', 1);
SET IDENTITY_INSERT department OFF;

-- 3. Insert Specializations
SET IDENTITY_INSERT specialization ON;
INSERT INTO specialization (specialization_id, name, description) VALUES 
(1, 'Cardiologist', 'Specialist in heart disease and cardiovascular health'),
(2, 'Pediatrician', 'Specialist in child health and growth'),
(3, 'Neurologist', 'Specialist in nervous system disorders'),
(4, 'Dermatologist', 'Specialist in skin conditions and aesthetics'),
(5, 'Orthopedic Surgeon', 'Specialist in bone and joint surgery');
SET IDENTITY_INSERT specialization OFF;

-- 4. Insert Staff & Doctor/Admin Users
SET IDENTITY_INSERT staff ON;
INSERT INTO staff (staff_id, name, email, phone, staff_type, hospital_id, department_id) 
VALUES (1, 'System Administrator', 'admin@lankacare.lk', '+94770000001', 'ADMIN', 1, 1);
INSERT INTO staff (staff_id, name, email, phone, staff_type, hospital_id, department_id) 
VALUES (2, 'Dr. Nadeesha Perera', 'dr.perera@lankacare.lk', '+94770000002', 'DOCTOR', 1, 1);
INSERT INTO staff (staff_id, name, email, phone, staff_type, hospital_id, department_id) 
VALUES (3, 'Dr. Malithi Fernando', 'dr.fernando@lankacare.lk', '+94770000003', 'DOCTOR', 1, 2);
SET IDENTITY_INSERT staff OFF;

INSERT INTO admin_staff (staff_id, admin_level) VALUES (1, 'SUPER_ADMIN');

INSERT INTO doctor (staff_id, specialization, qualification, license_no) 
VALUES (2, 'Cardiologist', 'MBBS, MD (Cardiology), FRCP', 'SLMC-45892');

INSERT INTO doctor (staff_id, specialization, qualification, license_no) 
VALUES (3, 'Pediatrician', 'MBBS, DCH, MD (Pediatrics)', 'SLMC-51204');

-- 5. Insert Patient
SET IDENTITY_INSERT patient ON;
INSERT INTO patient (patient_id, name, email, phone, nic, dob, street, city, postal_code) 
VALUES (1, 'Kamal Silva', 'kamal@gmail.com', '+94711112233', '199512345678', '1995-05-15', '45 Galle Road', 'Colombo', '00300');
SET IDENTITY_INSERT patient OFF;

-- 6. Insert User Accounts (BCrypt Hash for password: 'password')
SET IDENTITY_INSERT user_account ON;
INSERT INTO user_account (user_id, username, password_hash, role, status, staff_id, patient_id) VALUES 
(1, 'admin', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'ADMIN', 'ACTIVE', 1, NULL),
(2, 'drperera', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'DOCTOR', 'ACTIVE', 2, NULL),
(3, 'drfernando', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'DOCTOR', 'ACTIVE', 3, NULL),
(4, 'kamal', '$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy', 'PATIENT', 'ACTIVE', NULL, 1);
SET IDENTITY_INSERT user_account OFF;

-- 7. Insert Sample Doctor Schedule
SET IDENTITY_INSERT doctor_schedule ON;
INSERT INTO doctor_schedule (schedule_id, doctor_id, department_id, schedule_date, start_time, end_time, max_patients, consultation_fee, status) VALUES 
(1, 2, 1, '2026-10-01', '09:00', '12:00', 10, 2500.00, 'ACTIVE'),
(2, 3, 2, '2026-10-01', '14:00', '17:00', 12, 2000.00, 'ACTIVE');
SET IDENTITY_INSERT doctor_schedule OFF;

-- 8. Insert Complaint Categories
SET IDENTITY_INSERT complaint_category ON;
INSERT INTO complaint_category (category_id, name, description) VALUES 
(1, 'Appointment Scheduling', 'Issues with booking, rescheduling, or cancelled appointments'),
(2, 'Doctor Service', 'Feedback or concerns regarding doctor consultation or behavior'),
(3, 'Payment & Billing', 'Issues with card payments, receipts, or refund requests'),
(4, 'Facility & Staff', 'Feedback regarding hospital facilities or administrative staff');
SET IDENTITY_INSERT complaint_category OFF;

-- 9. Insert Medications Catalog
SET IDENTITY_INSERT medication ON;
INSERT INTO medication (medication_id, name, brand, dosage_form, unit) VALUES 
(1, 'Paracetamol', 'Panadol', 'Tablet', '500mg'),
(2, 'Amoxicillin', 'Amoxil', 'Capsule', '250mg'),
(3, 'Atorvastatin', 'Lipitor', 'Tablet', '10mg'),
(4, 'Metformin', 'Glucophage', 'Tablet', '500mg');
SET IDENTITY_INSERT medication OFF;

-- 10. Insert System Settings
SET IDENTITY_INSERT system_setting ON;
INSERT INTO system_setting (setting_id, setting_key, setting_value, description) VALUES 
(1, 'SYSTEM_NAME', 'LankaCare E-Channeling System', 'Application display title'),
(2, 'CURRENCY', 'LKR', 'Default transaction currency'),
(3, 'MAX_BOOKING_ADVANCE_DAYS', '30', 'Maximum days in advance a patient can book an appointment');
SET IDENTITY_INSERT system_setting OFF;
