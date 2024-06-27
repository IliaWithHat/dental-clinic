package org.ilia.userservice.controller.response;

import lombok.Value;
import org.ilia.userservice.enums.Role;

import java.time.LocalDate;
import java.util.UUID;

@Value
public class UserDto {

    UUID id;
    String email;
    String firstName;
    String lastName;
    LocalDate birthDate;
    String phoneNumber;
    Boolean isWorking;
    Role role;
}
