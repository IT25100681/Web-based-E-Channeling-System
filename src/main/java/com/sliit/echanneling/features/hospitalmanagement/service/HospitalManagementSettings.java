package com.sliit.echanneling.features.hospitalmanagement.service;

import com.sliit.echanneling.features.hospitalmanagement.dto.SystemSettingForm;
import com.sliit.echanneling.features.hospitalmanagement.util.InputSanitizer;
import com.sliit.echanneling.model.SystemSetting;
import com.sliit.echanneling.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Singleton Pattern: Spring creates this service as one shared bean, making it
 * the module-wide access point for system settings and backup/recovery flags.
 */
@Service
@RequiredArgsConstructor
public class HospitalManagementSettings {

    public static final String BACKUP_STATUS_KEY = "hospital.management.backup.status";
    public static final String RECOVERY_STATUS_KEY = "hospital.management.recovery.status";

    private final SystemSettingRepository systemSettingRepository;
    private final InputSanitizer sanitizer;

    @Transactional(readOnly = true)
    public List<SystemSetting> getAllSettings() {
        return systemSettingRepository.findAll();
    }

    @Transactional
    public SystemSetting upsert(SystemSettingForm form) {
        String key = sanitizer.clean(form.getSettingKey());
        SystemSetting setting = systemSettingRepository.findBySettingKey(key)
                .orElseGet(SystemSetting::new);
        setting.setSettingKey(key);
        setting.setSettingValue(sanitizer.clean(form.getSettingValue()));
        setting.setDescription(sanitizer.cleanNullable(form.getDescription()));
        return systemSettingRepository.save(setting);
    }

    @Transactional
    public void markBackup(String value) {
        upsertSystemValue(BACKUP_STATUS_KEY, value, "Last hospital-management backup operation status");
    }

    @Transactional
    public void markRecovery(String value) {
        upsertSystemValue(RECOVERY_STATUS_KEY, value, "Last hospital-management recovery operation status");
    }

    private void upsertSystemValue(String key, String value, String description) {
        SystemSetting setting = systemSettingRepository.findBySettingKey(key)
                .orElseGet(SystemSetting::new);
        setting.setSettingKey(key);
        setting.setSettingValue(sanitizer.clean(value));
        setting.setDescription(description);
        systemSettingRepository.save(setting);
    }
}
