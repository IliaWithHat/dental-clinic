package org.ilia.appointmentservice.entity;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class WorkingTime {

    private UUID id;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
    private Integer timeIntervalInMinutes;
    private UUID doctorId;
}
