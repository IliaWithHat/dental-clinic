package org.ilia.mailschedulerservice.feign.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AppointmentDto {

    UUID id;
    LocalDateTime date;
    Boolean isPatientCome;
    String serviceInfo;
    Integer price;
    UUID patientId;
    UUID doctorId;
}
