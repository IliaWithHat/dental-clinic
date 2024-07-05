package org.ilia.userservice.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Value;

import java.time.LocalDate;

@Value
public class UpdateUserDto {

    @NotBlank(message = "email must not be blank")
    @Email(message = "not a well-formed email address")
    String email;

    @NotBlank(message = "firstName must not be blank")
    String firstName;

    @NotBlank(message = "lastName must not be blank")
    String lastName;

    @NotNull(message = "birthDate must not be null")
    @Past(message = "birthDate must be a past date")
    LocalDate birthDate;

    @NotBlank(message = "phoneNumber must not be blank")
    String phoneNumber;

    Boolean isWorking;
}
