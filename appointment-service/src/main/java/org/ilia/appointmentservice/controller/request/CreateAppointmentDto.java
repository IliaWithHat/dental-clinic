package org.ilia.appointmentservice.controller.request;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class CreateAppointmentDto {

    LocalDateTime date;
    UUID patientId;
    UUID doctorId;
}
