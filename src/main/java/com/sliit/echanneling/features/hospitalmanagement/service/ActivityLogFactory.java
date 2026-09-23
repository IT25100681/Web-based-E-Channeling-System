package com.sliit.echanneling.features.hospitalmanagement.service;

import com.sliit.echanneling.features.hospitalmanagement.model.HospitalManagementActivityLog;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Factory Pattern: centralizes construction of operation log entries so create,
 * update, deactivate, backup, and recovery events have consistent metadata.
 */
@Component
public class ActivityLogFactory {

    public HospitalManagementActivityLog create(String entityType, Long entityId, String action, String message) {
        return HospitalManagementActivityLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .message(message)
                .actor(currentActor())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }
        return authentication.getName();
    }
}
