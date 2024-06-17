package org.ilia.appointmentservice.controller.request;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class UpdateAppointmentRequest {

    Boolean isPatientCome;
    String serviceInfo;
    Integer price;
}
