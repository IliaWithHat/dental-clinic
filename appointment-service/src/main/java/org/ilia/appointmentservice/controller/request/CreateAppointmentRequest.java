package org.ilia.appointmentservice.controller.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentRequest {

    private LocalDateTime date;
    private Integer patientId;
    private Integer doctorId;
}
