-- V1__baseline.sql: Initial Database Schema for Web-based E-Channeling System (SQL Server DDL)

-- 1. Hospital
CREATE TABLE hospital (
    hospital_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    street VARCHAR(255),
    city VARCHAR(255),
    postal_code VARCHAR(50),
    contact_no VARCHAR(50)
);

-- 2. Department
CREATE TABLE department (
    department_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    hospital_id BIGINT NOT NULL,
    CONSTRAINT fk_dept_hospital FOREIGN KEY (hospital_id) REFERENCES hospital(hospital_id)
);

-- 3. Staff (Base table for JOINED inheritance)
CREATE TABLE staff (
    staff_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    staff_type VARCHAR(50) NOT NULL,
    hospital_id BIGINT,
    department_id BIGINT,
    supervisor_id BIGINT,
    CONSTRAINT fk_staff_hospital FOREIGN KEY (hospital_id) REFERENCES hospital(hospital_id),
    CONSTRAINT fk_staff_dept FOREIGN KEY (department_id) REFERENCES department(department_id),
    CONSTRAINT fk_staff_supervisor FOREIGN KEY (supervisor_id) REFERENCES staff(staff_id)
);

-- 4. Sub-staff tables (ISA Hierarchy)
CREATE TABLE doctor (
    staff_id BIGINT PRIMARY KEY,
    specialization VARCHAR(255),
    qualification VARCHAR(255),
    license_no VARCHAR(100),
    CONSTRAINT fk_doctor_staff FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE
);

CREATE TABLE nurse (
    staff_id BIGINT PRIMARY KEY,
    ward VARCHAR(100),
    qualification VARCHAR(255),
    CONSTRAINT fk_nurse_staff FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE
);

CREATE TABLE receptionist (
    staff_id BIGINT PRIMARY KEY,
    desk_no VARCHAR(50),
    CONSTRAINT fk_receptionist_staff FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE
);

CREATE TABLE pharmacist (
    staff_id BIGINT PRIMARY KEY,
    qualification VARCHAR(255),
    CONSTRAINT fk_pharmacist_staff FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE
);

CREATE TABLE admin_staff (
    staff_id BIGINT PRIMARY KEY,
    admin_level VARCHAR(50),
    CONSTRAINT fk_admin_staff FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE
);

-- 5. Patient
CREATE TABLE patient (
    patient_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    nic VARCHAR(50),
    dob VARCHAR(20),
    street VARCHAR(255),
    city VARCHAR(255),
    postal_code VARCHAR(50)
);

-- 6. User Account (Single login table with mutually exclusive FKs & filtered unique indexes for SQL Server)
CREATE TABLE user_account (
    user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    staff_id BIGINT,
    patient_id BIGINT,
    CONSTRAINT fk_user_staff FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
    CONSTRAINT fk_user_patient FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
);

CREATE UNIQUE INDEX uq_user_staff ON user_account(staff_id) WHERE staff_id IS NOT NULL;
CREATE UNIQUE INDEX uq_user_patient ON user_account(patient_id) WHERE patient_id IS NOT NULL;

-- 7. Specialization Catalog
CREATE TABLE specialization (
    specialization_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500)
);

-- 8. Doctor Schedule
CREATE TABLE doctor_schedule (
    schedule_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    department_id BIGINT,
    schedule_date VARCHAR(20) NOT NULL,
    start_time VARCHAR(20) NOT NULL,
    end_time VARCHAR(20) NOT NULL,
    max_patients INT NOT NULL DEFAULT 15,
    consultation_fee DECIMAL(10, 2) NOT NULL DEFAULT 2000.00,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_schedule_doctor FOREIGN KEY (doctor_id) REFERENCES staff(staff_id),
    CONSTRAINT fk_schedule_dept FOREIGN KEY (department_id) REFERENCES department(department_id),
    CONSTRAINT uq_doctor_slot UNIQUE (doctor_id, schedule_date, start_time)
);

-- 9. Appointment
CREATE TABLE appointment (
    appointment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    schedule_id BIGINT NOT NULL,
    appointment_date VARCHAR(20) NOT NULL,
    appointment_time VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reference_no VARCHAR(100) NOT NULL UNIQUE,
    created_at VARCHAR(50),
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patient(patient_id),
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES staff(staff_id),
    CONSTRAINT fk_appointment_schedule FOREIGN KEY (schedule_id) REFERENCES doctor_schedule(schedule_id),
    CONSTRAINT uq_appointment_slot UNIQUE (doctor_id, appointment_date, appointment_time)
);

-- 10. Payment
CREATE TABLE payment (
    payment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    transaction_ref VARCHAR(100) NOT NULL UNIQUE,
    payment_type VARCHAR(50) NOT NULL DEFAULT 'PAYMENT',
    created_at VARCHAR(50),
    CONSTRAINT fk_payment_appointment FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id)
);

-- 11. Medication
CREATE TABLE medication (
    medication_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255),
    dosage_form VARCHAR(100),
    unit VARCHAR(50)
);

-- 12. Prescription
CREATE TABLE prescription (
    prescription_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    appointment_id BIGINT NOT NULL UNIQUE,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    issue_date VARCHAR(50) NOT NULL,
    notes VARCHAR(1000),
    CONSTRAINT fk_prescription_appointment FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id),
    CONSTRAINT fk_prescription_doctor FOREIGN KEY (doctor_id) REFERENCES staff(staff_id),
    CONSTRAINT fk_prescription_patient FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
);

-- 13. Prescription Item
CREATE TABLE prescription_item (
    item_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    medication_id BIGINT NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration_days INT NOT NULL,
    instructions VARCHAR(500),
    CONSTRAINT fk_item_prescription FOREIGN KEY (prescription_id) REFERENCES prescription(prescription_id) ON DELETE CASCADE,
    CONSTRAINT fk_item_medication FOREIGN KEY (medication_id) REFERENCES medication(medication_id)
);

-- 14. Medical History
CREATE TABLE medical_history (
    history_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    patient_id BIGINT NOT NULL UNIQUE,
    doctor_id BIGINT,
    appointment_id BIGINT,
    blood_group VARCHAR(20),
    chronic_conditions VARCHAR(1000),
    created_at VARCHAR(50),
    CONSTRAINT fk_history_patient FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
);

-- 15. Clinical Record
CREATE TABLE record (
    record_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    history_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    record_date VARCHAR(50) NOT NULL,
    diagnosis VARCHAR(1000) NOT NULL,
    treatment_notes VARCHAR(2000),
    CONSTRAINT fk_record_history FOREIGN KEY (history_id) REFERENCES medical_history(history_id) ON DELETE CASCADE,
    CONSTRAINT fk_record_doctor FOREIGN KEY (doctor_id) REFERENCES staff(staff_id)
);

-- 16. Record Attachment
CREATE TABLE record_attachment (
    attachment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    record_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_path VARCHAR(500) NOT NULL,
    upload_date VARCHAR(50),
    CONSTRAINT fk_attachment_record FOREIGN KEY (record_id) REFERENCES record(record_id) ON DELETE CASCADE
);

-- 17. Allergy
CREATE TABLE allergy (
    allergy_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    allergen VARCHAR(255) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    notes VARCHAR(500),
    CONSTRAINT fk_allergy_patient FOREIGN KEY (patient_id) REFERENCES patient(patient_id) ON DELETE CASCADE
);

-- 18. Complaint Category
CREATE TABLE complaint_category (
    category_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500)
);

-- 19. Complaint
CREATE TABLE complaint (
    complaint_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    handled_by BIGINT,
    category_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED',
    resolution_notes VARCHAR(2000),
    created_at VARCHAR(50),
    updated_at VARCHAR(50),
    CONSTRAINT fk_complaint_patient FOREIGN KEY (patient_id) REFERENCES patient(patient_id),
    CONSTRAINT fk_complaint_staff FOREIGN KEY (handled_by) REFERENCES staff(staff_id),
    CONSTRAINT fk_complaint_category FOREIGN KEY (category_id) REFERENCES complaint_category(category_id)
);

-- 20. Notification
CREATE TABLE notification (
    notification_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    channel VARCHAR(50) NOT NULL DEFAULT 'IN_APP',
    status VARCHAR(50) NOT NULL DEFAULT 'UNREAD',
    failure_reason VARCHAR(500),
    created_at VARCHAR(50),
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES user_account(user_id) ON DELETE CASCADE
);

-- 21. System Setting
CREATE TABLE system_setting (
    setting_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value VARCHAR(500) NOT NULL,
    description VARCHAR(255)
);
