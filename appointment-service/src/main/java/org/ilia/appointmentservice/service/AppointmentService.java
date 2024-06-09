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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    public Appointment create(CreateAppointmentRequest createAppointmentRequest, Role role, String userId) {
        return appointmentRepository.save(appointmentMapper.toAppointment(createAppointmentRequest));
    }

    public Appointment update(UpdateAppointmentRequest updateAppointmentRequest, UUID appointmentId, Role role, String userId) {
        return appointmentRepository.findById(appointmentId)
                .map(oldAppointment -> appointmentMapper.updateAppointment(updateAppointmentRequest, oldAppointment))
                .map(appointmentRepository::save)
                .orElseThrow();
    }

    public List<Appointment> find(DateRange dateRange, State state, Role role, String userId) {
        return null;
    }

    public void delete(UUID appointmentId, Role role, String userId) {
        appointmentRepository.deleteById(appointmentId);
    }
}
