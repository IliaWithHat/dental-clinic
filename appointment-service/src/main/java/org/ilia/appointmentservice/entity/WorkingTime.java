package org.ilia.appointmentservice.entity;

import lombok.Data;
import org.ilia.appointmentservice.enums.Day;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class WorkingTime {

    private UUID id;
    private Day day;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer timeIntervalInMinutes;
    private UUID doctorId;
}
