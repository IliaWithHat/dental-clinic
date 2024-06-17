package org.ilia.timeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = PRIVATE)
public class WorkingTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Enumerated(EnumType.STRING)
    DayOfWeek day;

    LocalTime startTime;

    LocalTime endTime;

    LocalTime breakStartTime;

    LocalTime breakEndTime;

    Integer timeIntervalInMinutes;

    UUID doctorId;
}
