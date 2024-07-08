package org.ilia.appointmentservice.controller.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class CreateAppointmentDto {

    @NotNull(message = "date must not be null")
    @Future(message = "date must be a future date")
    LocalDateTime date;
}
