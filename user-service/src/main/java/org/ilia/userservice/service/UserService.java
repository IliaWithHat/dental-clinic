package org.ilia.userservice.service;

import lombok.RequiredArgsConstructor;
import org.ilia.userservice.controller.request.CreateUserRequest;
import org.ilia.userservice.controller.request.LoginRequest;
import org.ilia.userservice.controller.request.SignUpRequest;
import org.ilia.userservice.controller.response.CreateUserResponse;
import org.ilia.userservice.controller.response.LoginResponse;
import org.ilia.userservice.controller.response.SignUpResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakService keycloakService;

    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        return new SignUpResponse(keycloakService.createPatient(signUpRequest));
    }

    public CreateUserResponse create(CreateUserRequest createUserRequest) {
        return new CreateUserResponse(keycloakService.createDoctor(createUserRequest));
    }

    public LoginResponse login(LoginRequest loginRequest) {
        return new LoginResponse(keycloakService.getAccessToken(loginRequest));
    }

    public void delete(String id) {
        keycloakService.deleteUser(id);
    }
}
