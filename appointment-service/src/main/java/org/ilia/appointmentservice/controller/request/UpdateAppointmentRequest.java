package org.ilia.appointmentservice.controller.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateAppointmentRequest {

    private Integer id;
    private LocalDateTime date;
    private Boolean isPatientCome;
    private String serviceInfo;
    private Integer price;
    private Integer patientId;
    private Integer doctorId;
}
