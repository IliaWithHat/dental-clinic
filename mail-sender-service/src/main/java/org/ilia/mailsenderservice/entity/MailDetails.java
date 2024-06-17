package org.ilia.mailsenderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.mailsenderservice.enums.Subject;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
