package org.ilia.timeservice.controller.request;

import lombok.Data;
import org.ilia.timeservice.enums.Day;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class CreateWorkingTimeRequest {

    private Day day;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer timeIntervalInMinutes;
    private UUID doctorId;
}
