package org.ilia.timeservice.controller.request;

import lombok.Value;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Value
public class CreateWorkingTimeDto {

    DayOfWeek day;
    LocalTime startTime;
    LocalTime endTime;
    LocalTime breakStartTime;
    LocalTime breakEndTime;
    Integer timeIntervalInMinutes;
}
