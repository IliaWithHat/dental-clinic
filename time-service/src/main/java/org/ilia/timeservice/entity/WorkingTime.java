package org.ilia.timeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ilia.timeservice.enums.Day;

import java.time.LocalTime;

import static jakarta.persistence.GenerationType.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class WorkingTime {

    @Id
    @GeneratedValue(strategy = UUID)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Day day;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer timeIntervalInMinutes;

    private Integer doctorId;
}
