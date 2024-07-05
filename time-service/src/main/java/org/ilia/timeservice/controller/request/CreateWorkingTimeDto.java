package org.ilia.timeservice.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.ilia.timeservice.validation.annotation.CorrectTime;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Value
@CorrectTime
public class CreateWorkingTimeDto {

    @NotNull(message = "day must not be null")
    DayOfWeek day;

    @NotNull(message = "startTime must not be null")
    LocalTime startTime;

    @NotNull(message = "endTime must not be null")
    LocalTime endTime;

    LocalTime breakStartTime;

    LocalTime breakEndTime;

    @NotNull(message = "timeIntervalInMinutes must not be null")
    Integer timeIntervalInMinutes;
}
