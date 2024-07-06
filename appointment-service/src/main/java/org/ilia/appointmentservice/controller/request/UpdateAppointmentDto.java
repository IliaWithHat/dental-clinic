package org.ilia.appointmentservice.controller.request;

import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.ilia.appointmentservice.validation.annotation.ValidUpdateAppointment;

@Value
@ValidUpdateAppointment
public class UpdateAppointmentDto {

    Boolean isPatientCome;

    String serviceInfo;

    @Positive
    Integer price;
}
