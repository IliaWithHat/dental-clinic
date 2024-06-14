package org.ilia.mailsenderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ilia.mailsenderservice.enums.Subject;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailDetails {

    private Subject subject;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private String doctorFirstName;
    private String doctorLastName;
    private LocalDateTime appointmentDate;
}
