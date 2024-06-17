package org.ilia.appointmentservice.entity;

import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.ilia.appointmentservice.enums.Role;

import java.time.LocalDate;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class User {

    UUID id;
    String email;
    String firstName;
    String lastName;
    LocalDate birthDate;
    String phoneNumber;
    String isWorking;
    String password;
    Role role;
}
