package org.ilia.appointmentservice.mapper;

import org.ilia.appointmentservice.controller.request.CreateAppointmentDto;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentDto;
import org.ilia.appointmentservice.controller.response.AppointmentDto;
import org.ilia.appointmentservice.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface AppointmentMapper {

    Appointment toAppointment(CreateAppointmentDto createAppointmentDto);

    Appointment updateAppointment(UpdateAppointmentDto updateAppointmentDto, @MappingTarget Appointment appointment);

    AppointmentDto toAppointmentDto(Appointment appointment);

    AppointmentDto toAppointmentDto(LocalDateTime date, UUID doctorId);
}
