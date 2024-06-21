package org.ilia.appointmentservice.feign.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
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
