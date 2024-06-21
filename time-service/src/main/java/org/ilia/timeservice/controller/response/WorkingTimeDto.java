package org.ilia.timeservice.controller.response;

import lombok.Value;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Value
public class WorkingTimeDto {

    UUID id;
    DayOfWeek day;
    LocalTime startTime;
    LocalTime endTime;
    LocalTime breakStartTime;
    LocalTime breakEndTime;
    Integer timeIntervalInMinutes;
    UUID doctorId;
}
