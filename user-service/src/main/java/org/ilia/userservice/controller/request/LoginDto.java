package org.ilia.userservice.controller.request;

import lombok.Value;

@Value
public class LoginDto {

    String email;
    String password;
}
