package org.ilia.appointmentservice.controller.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateAppointmentRequest {

    private LocalDateTime date;
    private UUID patientId;
    private UUID doctorId;
}
