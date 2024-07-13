package org.ilia.userservice.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.ilia.userservice.enums.Subject;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class MailDetails {

    Subject subject;
    String patientEmail;
    String patientFirstName;
    String patientLastName;
}
