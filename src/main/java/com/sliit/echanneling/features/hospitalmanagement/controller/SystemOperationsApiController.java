package com.sliit.echanneling.features.hospitalmanagement.controller;

import com.sliit.echanneling.features.hospitalmanagement.service.HospitalManagementService;
import com.sliit.echanneling.features.hospitalmanagement.service.HospitalSystemBackupExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;

@RestController
@RequestMapping("/api/system-operations")
@RequiredArgsConstructor
public class SystemOperationsApiController {

    private final HospitalManagementService hospitalManagementService;
    private final HospitalSystemBackupExportService backupExportService;

    @PostMapping("/record-backup")
    public ResponseEntity<Map<String, String>> recordBackup() {
        try {
            hospitalManagementService.recordBackup();
            return ResponseEntity.ok(Map.of("status", "ok", "message", "Backup checkpoint recorded."));
        } catch (DataAccessException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error", "message", "Could not record backup checkpoint."));
        }
    }

    /**
     * Streams the hospital-management backup as JSON with attachment headers.
     * The stream delegates database paging to HospitalSystemBackupExportService.
     */
    @GetMapping("/download-backup")
    public ResponseEntity<StreamingResponseBody> downloadBackup() {
        String filename = backupExportService.backupFileName();
        StreamingResponseBody stream = outputStream -> backupExportService.writeBackup(outputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(stream);
    }
}
