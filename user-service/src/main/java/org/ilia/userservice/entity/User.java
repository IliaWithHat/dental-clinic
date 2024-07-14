package org.ilia.userservice.entity;

import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.ilia.userservice.enums.Role;

import java.time.LocalDate;
import java.util.UUID;

import static java.lang.Boolean.FALSE;
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
    Boolean isWorking;
    Boolean isDeleted = FALSE;
    String password;
    Role role;
}
