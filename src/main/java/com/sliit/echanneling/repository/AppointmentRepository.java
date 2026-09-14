package com.sliit.echanneling.repository;

import com.sliit.echanneling.model.Appointment;
import com.sliit.echanneling.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient_PatientId(Long patientId);
    List<Appointment> findByDoctor_StaffId(Long doctorId);
    Optional<Appointment> findByReferenceNo(String referenceNo);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.schedule.scheduleId = :scheduleId AND a.status IN ('PENDING', 'CONFIRMED')")
    long countActiveBookingsBySchedule(@Param("scheduleId") Long scheduleId);
}
