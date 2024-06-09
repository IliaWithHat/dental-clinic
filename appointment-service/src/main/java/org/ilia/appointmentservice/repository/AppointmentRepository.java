package org.ilia.appointmentservice.repository;

import org.ilia.appointmentservice.controller.request.DateRange;
import org.ilia.appointmentservice.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Query("from Appointment a where a.patientId = :patientId and a.date between :from and :to")
    List<Appointment> findByPatientIdAndDateRange(UUID patientId, LocalDate from, LocalDate to);

    @Query("from Appointment a where a.doctorId = :doctorId and a.date between :from and :to")
    List<Appointment> findByDoctorIdAndDateRange(UUID doctorId, LocalDate from, LocalDate to);
}
