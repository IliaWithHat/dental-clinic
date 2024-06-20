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
public class FindAppointmentResponse {

    LocalDateTime date;
    Boolean isPatientCome;
    UUID patientId;
    UUID doctorId;
}
