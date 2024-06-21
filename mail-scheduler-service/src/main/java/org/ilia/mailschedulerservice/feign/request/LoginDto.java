package org.ilia.mailschedulerservice.feign.request;

import lombok.Value;

@Value
public class LoginDto {

    String email;
    String password;
}
