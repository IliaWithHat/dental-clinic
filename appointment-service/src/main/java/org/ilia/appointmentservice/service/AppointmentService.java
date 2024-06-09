package org.ilia.appointmentservice.service;

import lombok.RequiredArgsConstructor;
import org.ilia.appointmentservice.controller.request.CreateAppointmentRequest;
import org.ilia.appointmentservice.controller.request.DateRange;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentRequest;
import org.ilia.appointmentservice.entity.Appointment;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.enums.State;
import org.ilia.appointmentservice.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public Appointment create(CreateAppointmentRequest appointment, Role role, String userId) {
        return null;
    }

    public Appointment update(UpdateAppointmentRequest appointment, String appointmentId, Role role, String userId) {
        return null;
    }

    public List<Appointment> find(DateRange dateRange, State state, Role role, String userId) {
        return null;
    }

    public void delete(String appointmentId, Role role, String userId) {
    }
}
