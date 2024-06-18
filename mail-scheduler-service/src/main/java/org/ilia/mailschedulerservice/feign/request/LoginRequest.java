package org.ilia.mailschedulerservice.feign.request;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class LoginRequest {

    String email;
    String password;
}
