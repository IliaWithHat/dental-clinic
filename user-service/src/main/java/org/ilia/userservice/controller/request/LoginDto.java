package org.ilia.userservice.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class LoginDto {

    @NotBlank
    @Email
    String email;

    @NotBlank
    String password;
}
