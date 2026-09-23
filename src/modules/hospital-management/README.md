# Hospital Management Module

This folder contains frontend-facing assets and notes for the Hospitals, Departments & Specializations module.

## Backup Export API

- `POST /api/system-operations/record-backup`
  - Records the backup operation in Recent Activity.
  - Updates `hospital.management.backup.status`.

- `GET /api/system-operations/download-backup`
  - Streams a JSON database export.
  - Returns `Content-Type: application/json`.
  - Returns `Content-Disposition: attachment; filename="hospital_system_backup_<TIMESTAMP>.json"`.

The Spring implementation lives in:

- `src/main/java/com/sliit/echanneling/features/hospitalmanagement/controller/SystemOperationsApiController.java`
- `src/main/java/com/sliit/echanneling/features/hospitalmanagement/service/HospitalSystemBackupExportService.java`

## Frontend

- `components/SettingsAndOperations.jsx`

The current project is Spring Boot + Thymeleaf, so the production page at
`src/main/resources/templates/features/hospital-management/index.html` also calls the same API endpoints with `fetch()`.

## Error Handling

The backend streams records in repository pages to avoid building a full export string in memory. Database access failures are handled by the API with clear error responses where possible, and the frontend shows non-blocking toast messages through `onToast`.
