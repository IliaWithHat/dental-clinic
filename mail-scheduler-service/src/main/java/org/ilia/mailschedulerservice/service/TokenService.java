package org.ilia.mailschedulerservice.service;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.ilia.mailschedulerservice.feign.UserServiceClient;
import org.ilia.mailschedulerservice.feign.request.LoginDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.mailschedulerservice.enums.Role.OWNER;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TokenService {

    UserServiceClient userServiceClient;
    String email;
    String password;

    @Getter
    @NonFinal
    String token;

    public TokenService(UserServiceClient userServiceClient,
                        @Value("${keycloak.email}") String email,
                        @Value("${keycloak.password}") String password) {
        this.userServiceClient = userServiceClient;
        this.email = email;
        this.password = password;
    }

    public void initializeToken() {
        token = userServiceClient.login(OWNER, new LoginDto(email, password)).getToken();
    }
}
