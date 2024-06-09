package org.ilia.appointmentservice.service;

import lombok.RequiredArgsConstructor;
import org.ilia.appointmentservice.controller.request.CreateAppointmentRequest;
import org.ilia.appointmentservice.controller.request.DateRange;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentRequest;
import org.ilia.appointmentservice.entity.Appointment;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.enums.State;
import org.ilia.appointmentservice.mapper.AppointmentMapper;
import org.ilia.appointmentservice.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import static org.ilia.appointmentservice.enums.Role.DOCTOR;
import static org.ilia.appointmentservice.enums.Role.PATIENT;
import static org.ilia.appointmentservice.enums.State.FREE;
import static org.ilia.appointmentservice.enums.State.OCCUPIED;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    public Appointment create(CreateAppointmentRequest createAppointmentRequest, Role role, UUID userId) {
        return appointmentRepository.save(appointmentMapper.toAppointment(createAppointmentRequest));
    }

    public Appointment update(UpdateAppointmentRequest updateAppointmentRequest, UUID appointmentId, Role role, UUID userId) {
        return appointmentRepository.findById(appointmentId)
                .map(oldAppointment -> appointmentMapper.updateAppointment(updateAppointmentRequest, oldAppointment))
                .map(appointmentRepository::save)
                .orElseThrow();
    }

    public List<Appointment> find(DateRange dateRange, State state, Role role, UUID userId) {
        if (dateRange.equals(new DateRange())) {
            initializeDateRange(dateRange);
        }
        if (role == PATIENT) {
            return appointmentRepository.findByPatientId(userId);
        }
        if (role == DOCTOR && state == OCCUPIED) {
            return appointmentRepository.findByDoctorId(userId);
        }
        if (role == DOCTOR && state == FREE) {
            //TODO request to working time microservices
        }
        throw new RuntimeException();
    }

    public void delete(UUID appointmentId, Role role, UUID userId) {
        appointmentRepository.deleteById(appointmentId);
    }

    private void initializeDateRange(DateRange dateRange) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();
        dateRange.setFrom(LocalDate.of(year, month, 1));
        dateRange.setTo(LocalDate.of(year, month.plus(1), 1));
    }
}
