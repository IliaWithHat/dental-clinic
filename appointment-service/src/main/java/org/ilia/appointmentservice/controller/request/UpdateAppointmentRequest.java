package org.ilia.appointmentservice.controller.request;

import lombok.Data;

@Data
public class UpdateAppointmentRequest {

    private Boolean isPatientCome;
    private String serviceInfo;
    private Integer price;
}
