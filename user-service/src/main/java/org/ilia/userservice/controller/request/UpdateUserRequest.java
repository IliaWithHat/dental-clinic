package org.ilia.userservice.controller.request;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class UpdateUserRequest {

    UUID id;
    String email;
    String firstName;
    String lastName;
    LocalDate birthDate;
    String phoneNumber;
    String isWorking;
}
