package org.ilia.userservice.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Value;

import java.time.LocalDate;

@Value
public class CreateUserDto {

    @NotBlank
    @Email
    String email;

    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    @NotNull
    @Past
    LocalDate birthDate;

    @NotBlank
    String phoneNumber;

    Boolean isWorking;

    @NotBlank
    String password;
}
