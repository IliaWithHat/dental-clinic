package org.ilia.appointmentservice.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.ilia.appointmentservice.enums.Subject;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class MailDetails {

    Subject subject;
    String patientEmail;
    String patientFirstName;
    String patientLastName;
    String doctorFirstName;
    String doctorLastName;
    LocalDateTime appointmentDate;
}
