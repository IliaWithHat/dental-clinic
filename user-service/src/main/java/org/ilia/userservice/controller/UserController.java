package org.ilia.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.userservice.controller.request.CreateUserRequest;
import org.ilia.userservice.controller.request.LoginRequest;
import org.ilia.userservice.controller.request.SignUpRequest;
import org.ilia.userservice.controller.response.CreateUserResponse;
import org.ilia.userservice.controller.response.LoginResponse;
import org.ilia.userservice.controller.response.SignUpResponse;
import org.ilia.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(signUpRequest));
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> create(@RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(createUserRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(userService.login(loginRequest));
    }
}
