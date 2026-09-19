package com.sliit.echanneling.service;

import com.sliit.echanneling.dto.request.BookingRequestDTO;
import com.sliit.echanneling.dto.request.RescheduleRequestDTO;
import com.sliit.echanneling.dto.response.AppointmentViewDTO;
import com.sliit.echanneling.model.Appointment;
import com.sliit.echanneling.model.Doctor;
import com.sliit.echanneling.model.DoctorSchedule;
import com.sliit.echanneling.model.Patient;

import com.sliit.echanneling.model.enums.AppointmentStatus;
import com.sliit.echanneling.repository.AppointmentRepository;
import com.sliit.echanneling.repository.DoctorScheduleRepository;
import com.sliit.echanneling.repository.PatientRepository;
import com.sliit.echanneling.repository.PaymentRepository;
import com.sliit.echanneling.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorScheduleRepository scheduleRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private DoctorSchedule schedule;
    private Patient patient;

    @BeforeEach
    void setUp() {
        Doctor doctor = new Doctor();
        doctor.setStaffId(5L);
        doctor.setName("Dr. Smith");
        doctor.setSpecialization("Cardiology");

        schedule = DoctorSchedule.builder()
                .scheduleId(1L)
                .doctor(doctor)
                .scheduleDate("2026-10-01")
                .startTime("09:00")
                .endTime("12:00")
                .maxPatients(5)
                .status("ACTIVE")
                .build();

        patient = Patient.builder()
                .patientId(10L)
                .name("Test Patient")
                .email("test@patient.com")
                .build();
    }

    @Test
    void testBookAppointment_Success() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setScheduleId(1L);
        request.setPatientId(10L);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.countActiveBookingsBySchedule(1L)).thenReturn(2L);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment app = invocation.getArgument(0);
            app.setAppointmentId(100L);
            return app;
        });

        Appointment created = appointmentService.bookAppointment(request);

        assertNotNull(created);
        assertEquals(AppointmentStatus.PENDING, created.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void testBookAppointment_ScheduleFull_ThrowsException() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setScheduleId(1L);
        request.setPatientId(10L);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.countActiveBookingsBySchedule(1L)).thenReturn(5L);

        assertThrows(IllegalStateException.class, () -> appointmentService.bookAppointment(request));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void testRescheduleAppointment_Success() {
        Appointment existing = Appointment.builder()
                .appointmentId(100L)
                .patient(patient)
                .doctor(schedule.getDoctor())
                .schedule(schedule)
                .appointmentDate("2026-10-01")
                .appointmentTime("09:00")
                .status(AppointmentStatus.PENDING)
                .referenceNo("APP-12345678")
                .build();

        DoctorSchedule newSchedule = DoctorSchedule.builder()
                .scheduleId(2L)
                .doctor(schedule.getDoctor())
                .scheduleDate("2026-10-05")
                .startTime("14:00")
                .endTime("17:00")
                .maxPatients(10)
                .status("ACTIVE")
                .build();

        RescheduleRequestDTO request = new RescheduleRequestDTO();
        request.setAppointmentId(100L);
        request.setNewScheduleId(2L);
        request.setAppointmentDate("2026-10-05");
        request.setAppointmentTime("14:00");

        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(existing));
        when(scheduleRepository.findById(2L)).thenReturn(Optional.of(newSchedule));
        when(appointmentRepository.countActiveBookingsBySchedule(2L)).thenReturn(1L);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentViewDTO result = appointmentService.rescheduleAppointment(100L, request, "testuser");

        assertNotNull(result);
        assertEquals("2026-10-05", result.getAppointmentDate());
        assertEquals("14:00", result.getAppointmentTime());
        verify(appointmentRepository, times(1)).save(existing);
    }

    @Test
    void testRescheduleAppointment_CancelledAppointment_ThrowsException() {
        Appointment existing = Appointment.builder()
                .appointmentId(100L)
                .status(AppointmentStatus.CANCELLED)
                .build();

        RescheduleRequestDTO request = new RescheduleRequestDTO();
        request.setAppointmentId(100L);
        request.setNewScheduleId(2L);

        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () -> appointmentService.rescheduleAppointment(100L, request, "testuser"));
        verify(appointmentRepository, never()).save(any());
    }
}
