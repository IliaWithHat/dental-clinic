package org.ilia.userservice.controller.request;

import lombok.Value;

import java.time.LocalDate;

@Value
public class UpdateUserDto {

    String email;
    String firstName;
    String lastName;
    LocalDate birthDate;
    String phoneNumber;
    String isWorking;
}
