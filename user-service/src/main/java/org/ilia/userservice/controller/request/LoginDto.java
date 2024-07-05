package org.ilia.userservice.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class LoginDto {

    @NotBlank(message = "email must not be blank")
    @Email(message = "not a well-formed email address")
    String email;

    @NotBlank(message = "password must not be blank")
    String password;
}
