package org.ilia.timeservice.controller.request;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class CreateWorkingTimeRequest {

    DayOfWeek day;
    LocalTime startTime;
    LocalTime endTime;
    LocalTime breakStartTime;
    LocalTime breakEndTime;
    Integer timeIntervalInMinutes;
    UUID doctorId;
}
