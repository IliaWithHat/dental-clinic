package org.ilia.appointmentservice.repository;

import org.ilia.appointmentservice.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Query("from Appointment a where a.patientId = :patientId")
    List<Appointment> findByPatientId(UUID patientId);

    @Query("from Appointment a where a.patientId = :patientId and a.date between :from and :to")
    List<Appointment> findByPatientIdAndDateRange(UUID patientId, LocalDateTime from, LocalDateTime to);

    @Query("from Appointment a where a.doctorId = :doctorId")
    List<Appointment> findByDoctorId(UUID doctorId);

    @Query("from Appointment a where a.doctorId = :doctorId and a.date between :from and :to")
    List<Appointment> findByDoctorIdAndDateRange(UUID doctorId, LocalDateTime from, LocalDateTime to);
}
