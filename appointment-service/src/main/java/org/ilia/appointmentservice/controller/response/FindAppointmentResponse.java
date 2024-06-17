package org.ilia.appointmentservice.controller.response;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class FindAppointmentResponse {

    LocalDateTime date;
    UUID patientId;
    UUID doctorId;
}
