package com.sliit.echanneling.model;

import com.sliit.echanneling.model.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appointment", uniqueConstraints = {
    @UniqueConstraint(name = "uq_appointment_slot", columnNames = {"doctor_id", "appointment_date", "appointment_time"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private DoctorSchedule schedule;

    @Column(name = "appointment_date", nullable = false)
    private String appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private String appointmentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(name = "reference_no", nullable = false, unique = true)
    private String referenceNo;

    @Column(name = "created_at")
    private String createdAt;
}
