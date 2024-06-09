package org.ilia.appointmentservice.controller.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FindAppointmentResponse {

    private LocalDateTime date;
    private UUID patientId;
    private UUID doctorId;
}
