package com.sliit.echanneling.repository;

import com.sliit.echanneling.model.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    List<DoctorSchedule> findByDoctor_StaffId(Long doctorId);
    List<DoctorSchedule> findByScheduleDate(String scheduleDate);
    List<DoctorSchedule> findByStatus(String status);

    @Query("SELECT s FROM DoctorSchedule s JOIN FETCH s.doctor d WHERE (:specialization IS NULL OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))) AND (:date IS NULL OR s.scheduleDate = :date) AND s.status = 'ACTIVE'")
    List<DoctorSchedule> searchAvailableSchedules(@Param("specialization") String specialization, @Param("date") String date);
}
