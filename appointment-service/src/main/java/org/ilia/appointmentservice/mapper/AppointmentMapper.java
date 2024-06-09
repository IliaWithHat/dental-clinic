package org.ilia.appointmentservice.mapper;

import org.ilia.appointmentservice.controller.request.CreateAppointmentRequest;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentRequest;
import org.ilia.appointmentservice.controller.response.FindAppointmentResponse;
import org.ilia.appointmentservice.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isPatientCome", ignore = true)
    @Mapping(target = "serviceInfo", ignore = true)
    @Mapping(target = "price", ignore = true)
    Appointment toAppointment(CreateAppointmentRequest createAppointmentRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "patientId", ignore = true)
    @Mapping(target = "doctorId", ignore = true)
    Appointment updateAppointment(UpdateAppointmentRequest updateAppointmentRequest, @MappingTarget Appointment appointment);

    FindAppointmentResponse toFindAppointmentResponse(Appointment appointment);
}
