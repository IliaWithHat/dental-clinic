package org.ilia.appointmentservice.controller.response;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class AppointmentDto {

    UUID id;
    LocalDateTime date;
    Boolean isPatientCome;
    String serviceInfo;
    Integer price;
    UUID patientId;
    UUID doctorId;
}
