package org.ilia.userservice.controller.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateUserRequest {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String isWorking;
}
