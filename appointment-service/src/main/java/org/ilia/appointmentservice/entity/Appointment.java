package org.ilia.appointmentservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    private LocalDateTime date;

    private Boolean isPatientCome;

    private String serviceInfo;

    private Integer price;

    private Integer patientId;

    private Integer doctorId;
}
