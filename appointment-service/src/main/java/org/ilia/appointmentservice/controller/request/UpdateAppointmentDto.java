package org.ilia.appointmentservice.controller.request;

import lombok.Value;

@Value
public class UpdateAppointmentDto {

    Boolean isPatientCome;
    String serviceInfo;
    Integer price;
}
