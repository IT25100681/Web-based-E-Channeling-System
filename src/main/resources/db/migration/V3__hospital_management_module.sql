ALTER TABLE hospital ADD hospital_code VARCHAR(50);
ALTER TABLE hospital ADD active BIT NOT NULL DEFAULT 1;

ALTER TABLE department ADD department_code VARCHAR(50);
ALTER TABLE department ADD active BIT NOT NULL DEFAULT 1;

ALTER TABLE specialization ADD specialization_code VARCHAR(50);
ALTER TABLE specialization ADD active BIT NOT NULL DEFAULT 1;

CREATE TABLE hospital_management_activity_log (
    log_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    entity_type VARCHAR(80) NOT NULL,
    entity_id BIGINT,
    action VARCHAR(80) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    actor VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);
