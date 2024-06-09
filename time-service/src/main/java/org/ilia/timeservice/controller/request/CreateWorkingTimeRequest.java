package org.ilia.timeservice.controller.request;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class CreateWorkingTimeRequest {

    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer timeIntervalInMinutes;
    private UUID doctorId;
}
