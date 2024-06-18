package org.ilia.mailschedulerservice.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.ilia.mailschedulerservice.feign.UserServiceClient;
import org.ilia.mailschedulerservice.feign.request.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.mailschedulerservice.enums.Role.OWNER;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TokenService {

    UserServiceClient userServiceClient;

    @Value("${keycloak.email}")
    @NonFinal
    @Setter
    String email;

    @Value("${keycloak.password}")
    @NonFinal
    @Setter
    String password;

    @NonFinal
    @Getter
    String token;

    public void initializeToken() {
        LoginRequest loginRequest = LoginRequest.builder().email(email).password(password).build();
        token = userServiceClient.login(loginRequest, OWNER).getToken();
    }
}
