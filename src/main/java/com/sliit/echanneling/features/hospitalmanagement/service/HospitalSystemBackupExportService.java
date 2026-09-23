package com.sliit.echanneling.features.hospitalmanagement.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.echanneling.features.hospitalmanagement.repository.HospitalManagementDepartmentRepository;
import com.sliit.echanneling.features.hospitalmanagement.repository.HospitalManagementHospitalRepository;
import com.sliit.echanneling.features.hospitalmanagement.repository.HospitalManagementSpecializationRepository;
import com.sliit.echanneling.model.Department;
import com.sliit.echanneling.model.Hospital;
import com.sliit.echanneling.model.Specialization;
import com.sliit.echanneling.model.SystemSetting;
import com.sliit.echanneling.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class HospitalSystemBackupExportService {

    private static final int PAGE_SIZE = 250;

    private final ObjectMapper objectMapper;
    private final HospitalManagementHospitalRepository hospitalRepository;
    private final HospitalManagementDepartmentRepository departmentRepository;
    private final HospitalManagementSpecializationRepository specializationRepository;
    private final SystemSettingRepository systemSettingRepository;

    public String backupFileName() {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
        return "hospital_system_backup_" + timestamp + ".json";
    }

    /**
     * Repository Pattern + streaming export: reads each repository in pages and
     * writes JSON directly to the response stream to avoid a large in-memory buffer.
     */
    @Transactional(readOnly = true)
    public void writeBackup(OutputStream outputStream) throws IOException {
        try (JsonGenerator json = objectMapper.getFactory().createGenerator(outputStream)) {
            json.writeStartObject();
            json.writeStringField("generatedAt", LocalDateTime.now().toString());
            writeHospitals(json);
            writeDepartments(json);
            writeSpecializations(json);
            writeSystemSettings(json);
            json.writeEndObject();
        }
    }

    private void writeHospitals(JsonGenerator json) throws IOException {
        json.writeArrayFieldStart("hospitals");
        int pageIndex = 0;
        Page<Hospital> page;
        do {
            page = hospitalRepository.findAll(PageRequest.of(pageIndex++, PAGE_SIZE));
            for (Hospital hospital : page.getContent()) {
                json.writeStartObject();
                json.writeNumberField("id", hospital.getHospitalId());
                writeNullableString(json, "code", hospital.getCode());
                json.writeStringField("name", hospital.getName());
                writeNullableString(json, "contactNo", hospital.getContactNo());
                json.writeBooleanField("active", Boolean.TRUE.equals(hospital.getActive()));
                json.writeObjectFieldStart("address");
                writeNullableString(json, "street", hospital.getAddress() == null ? null : hospital.getAddress().getStreet());
                writeNullableString(json, "city", hospital.getAddress() == null ? null : hospital.getAddress().getCity());
                writeNullableString(json, "postalCode", hospital.getAddress() == null ? null : hospital.getAddress().getPostalCode());
                json.writeEndObject();
                json.writeEndObject();
            }
            json.flush();
        } while (page.hasNext());
        json.writeEndArray();
    }

    private void writeDepartments(JsonGenerator json) throws IOException {
        json.writeArrayFieldStart("departments");
        int pageIndex = 0;
        Page<Department> page;
        do {
            page = departmentRepository.findAll(PageRequest.of(pageIndex++, PAGE_SIZE));
            for (Department department : page.getContent()) {
                json.writeStartObject();
                json.writeNumberField("id", department.getDepartmentId());
                writeNullableString(json, "code", department.getCode());
                json.writeStringField("name", department.getName());
                writeNullableString(json, "description", department.getDescription());
                json.writeBooleanField("active", Boolean.TRUE.equals(department.getActive()));
                if (department.getHospital() != null) {
                    json.writeObjectFieldStart("hospital");
                    json.writeNumberField("id", department.getHospital().getHospitalId());
                    writeNullableString(json, "code", department.getHospital().getCode());
                    json.writeStringField("name", department.getHospital().getName());
                    json.writeEndObject();
                } else {
                    json.writeNullField("hospital");
                }
                json.writeEndObject();
            }
            json.flush();
        } while (page.hasNext());
        json.writeEndArray();
    }

    private void writeSpecializations(JsonGenerator json) throws IOException {
        json.writeArrayFieldStart("specializations");
        int pageIndex = 0;
        Page<Specialization> page;
        do {
            page = specializationRepository.findAll(PageRequest.of(pageIndex++, PAGE_SIZE));
            for (Specialization specialization : page.getContent()) {
                json.writeStartObject();
                json.writeNumberField("id", specialization.getSpecializationId());
                writeNullableString(json, "code", specialization.getCode());
                json.writeStringField("name", specialization.getName());
                writeNullableString(json, "description", specialization.getDescription());
                json.writeBooleanField("active", Boolean.TRUE.equals(specialization.getActive()));
                json.writeEndObject();
            }
            json.flush();
        } while (page.hasNext());
        json.writeEndArray();
    }

    private void writeSystemSettings(JsonGenerator json) throws IOException {
        json.writeArrayFieldStart("systemSettings");
        int pageIndex = 0;
        Page<SystemSetting> page;
        do {
            page = systemSettingRepository.findAll(PageRequest.of(pageIndex++, PAGE_SIZE));
            for (SystemSetting setting : page.getContent()) {
                json.writeStartObject();
                json.writeNumberField("id", setting.getSettingId());
                json.writeStringField("key", setting.getSettingKey());
                json.writeStringField("value", setting.getSettingValue());
                writeNullableString(json, "description", setting.getDescription());
                json.writeEndObject();
            }
            json.flush();
        } while (page.hasNext());
        json.writeEndArray();
    }

    private void writeNullableString(JsonGenerator json, String field, String value) throws IOException {
        if (value == null) {
            json.writeNullField(field);
            return;
        }
        json.writeStringField(field, value);
    }
}
