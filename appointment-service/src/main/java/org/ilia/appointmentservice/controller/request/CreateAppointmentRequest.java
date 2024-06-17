package org.ilia.appointmentservice.controller.request;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class CreateAppointmentRequest {

    LocalDateTime date;
    UUID patientId;
    UUID doctorId;
}
