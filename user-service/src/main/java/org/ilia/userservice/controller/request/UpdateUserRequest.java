package org.ilia.userservice.controller.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String isWorking;
}
