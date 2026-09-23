package com.sliit.echanneling.features.hospitalmanagement;

import com.sliit.echanneling.features.hospitalmanagement.controller.SystemOperationsApiController;
import com.sliit.echanneling.features.hospitalmanagement.model.HospitalManagementActivityLog;
import com.sliit.echanneling.features.hospitalmanagement.repository.HospitalManagementActivityLogRepository;
import com.sliit.echanneling.features.hospitalmanagement.service.ActivityLogFactory;
import com.sliit.echanneling.features.hospitalmanagement.service.HospitalManagementService;
import com.sliit.echanneling.features.hospitalmanagement.service.HospitalManagementServiceImpl;
import com.sliit.echanneling.features.hospitalmanagement.service.HospitalManagementSettings;
import com.sliit.echanneling.features.hospitalmanagement.service.HospitalSystemBackupExportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalManagementBackupLoggingTest {

    @Mock
    private HospitalManagementSettings settings;

    @Mock
    private HospitalManagementActivityLogRepository activityLogRepository;

    @Mock
    private ActivityLogFactory activityLogFactory;

    @InjectMocks
    private HospitalManagementServiceImpl hospitalManagementService;

    @Mock
    private HospitalSystemBackupExportService backupExportService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recordBackup_updatesSettingsAndSavesAuditLog() {
        HospitalManagementActivityLog expectedLog = HospitalManagementActivityLog.builder()
                .entityType("SYSTEM")
                .action("BACKUP_RECORDED")
                .message("Recorded backup checkpoint: Recorded at now")
                .actor("system")
                .build();

        when(activityLogFactory.create(eq("SYSTEM"), isNull(), eq("BACKUP_RECORDED"), startsWith("Recorded backup checkpoint: Recorded at ")))
                .thenReturn(expectedLog);

        hospitalManagementService.recordBackup();

        verify(settings).markBackup(startsWith("Recorded at "));
        verify(activityLogRepository).save(expectedLog);
    }

    @Test
    void activityLogFactory_capturesCurrentAuthenticatedActor() {
        ActivityLogFactory factory = new ActivityLogFactory();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("super_admin", "password", java.util.Collections.emptyList())
        );

        HospitalManagementActivityLog log = factory.create("SYSTEM", null, "BACKUP_RECORDED", "Backup checkpoint");

        assertThat(log.getActor()).isEqualTo("super_admin");
        assertThat(log.getEntityType()).isEqualTo("SYSTEM");
        assertThat(log.getAction()).isEqualTo("BACKUP_RECORDED");
        assertThat(log.getMessage()).isEqualTo("Backup checkpoint");
        assertThat(log.getCreatedAt()).isNotNull();
    }

    @Test
    void activityLogFactory_defaultsToSystemWhenUnauthenticated() {
        ActivityLogFactory factory = new ActivityLogFactory();
        SecurityContextHolder.clearContext();

        HospitalManagementActivityLog log = factory.create("SYSTEM", null, "BACKUP_RECORDED", "Backup checkpoint");

        assertThat(log.getActor()).isEqualTo("system");
    }

    @Test
    void apiController_recordBackup_returnsOkWhenServiceSucceeds() {
        HospitalManagementService service = mock(HospitalManagementService.class);
        SystemOperationsApiController controller = new SystemOperationsApiController(service, backupExportService);

        ResponseEntity<Map<String, String>> response = controller.recordBackup();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("status", "ok");
        assertThat(response.getBody()).containsEntry("message", "Backup checkpoint recorded.");
        verify(service).recordBackup();
    }

    @Test
    void apiController_recordBackup_returns500WhenDataAccessExceptionOccurs() {
        HospitalManagementService service = mock(HospitalManagementService.class);
        doThrow(new DataRetrievalFailureException("DB connection dropped")).when(service).recordBackup();
        SystemOperationsApiController controller = new SystemOperationsApiController(service, backupExportService);

        ResponseEntity<Map<String, String>> response = controller.recordBackup();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("status", "error");
        assertThat(response.getBody()).containsEntry("message", "Could not record backup checkpoint.");
    }
}
