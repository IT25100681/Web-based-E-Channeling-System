# Hospitals, Departments & Specializations Module

This module implements the admin workflow for maintaining hospitals, departments, specializations, system settings, and operation logs.

## Isolated Module Paths

- Java feature package: `src/main/java/com/sliit/echanneling/features/hospitalmanagement/`
- Thymeleaf feature view: `src/main/resources/templates/features/hospital-management/index.html`
- Feature tests: `src/test/java/com/sliit/echanneling/features/hospitalmanagement/`
- Database migration: `src/main/resources/db/migration/V3__hospital_management_module.sql`

## Integration Touchpoints

The module is isolated behind the route `/admin/hospital-management`. The only intentional edits outside the feature package are:

- `src/main/java/com/sliit/echanneling/model/Hospital.java`: adds `code` and `active`.
- `src/main/java/com/sliit/echanneling/model/Department.java`: adds `code` and `active`.
- `src/main/java/com/sliit/echanneling/model/Specialization.java`: adds `code` and `active`.
- `src/main/resources/templates/fragments/nav.html`: points admin navigation to the module route.
- `src/main/resources/templates/admin/dashboard.html`: points the admin dashboard button to the module route.

## Implemented Scope

- Create, read/search, update, deactivate, restore, and safe-delete actions for hospitals.
- Create, read/search, update, deactivate, restore, and safe-delete actions for departments.
- Create, read/search, update, deactivate, restore, and safe-delete actions for specializations.
- System settings management through `HospitalManagementSettings`.
- Backup and recovery checkpoint recording.
- Activity logging for all major write operations.
- Inline server-side validation messages for create/settings forms.
- Client-side validation attributes for create and edit forms.
- Toast feedback, empty states, breadcrumbs, active tabs, and loading skeletons.
- Immediate UI refresh after writes through redirect-after-post and database refetch.

## Design Patterns

- Repository Pattern:
  - `HospitalManagementHospitalRepository`
  - `HospitalManagementDepartmentRepository`
  - `HospitalManagementSpecializationRepository`
  - These repositories isolate database access from controller and service code.

- Singleton Pattern:
  - `HospitalManagementSettings`
  - Spring manages this as a singleton service for module-wide settings access.

- Factory Pattern:
  - `ActivityLogFactory`
  - Centralizes activity-log object creation for consistent metadata.

## Environment Variables

Uses the project’s existing Spring configuration:

- `SPRING_PROFILES_ACTIVE`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_OPEN_BROWSER_ON_START`

## Setup

1. Ensure the database profile is configured.
2. Run migrations for profiles where Flyway is enabled.
3. Start the app.
4. Log in as an admin.
5. Open `/admin/hospital-management`.

## Branch Merge Instructions

1. Create a feature branch:
   `git checkout -b feature/hospital-management-module`
2. Commit only this module and its listed integration touchpoints.
3. Before merging, run:
   `mvn test`
4. Merge into your integration branch:
   `git checkout dev`
   `git merge --no-ff feature/hospital-management-module`
5. Resolve conflicts only in the listed integration touchpoints.
