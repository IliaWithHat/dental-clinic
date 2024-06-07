package org.ilia.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.userservice.controller.request.CreateUserRequest;
import org.ilia.userservice.controller.request.LoginRequest;
import org.ilia.userservice.controller.request.SignUpRequest;
import org.ilia.userservice.controller.request.UpdateUserRequest;
import org.ilia.userservice.controller.response.LoginResponse;
import org.ilia.userservice.entity.User;
import org.ilia.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<User> signUp(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.status(CREATED).body(userService.signUp(signUpRequest));
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.status(CREATED).body(userService.create(createUserRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok().body(userService.update(updateUserRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(userService.login(loginRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable String id) {
        return ResponseEntity.ok().body(userService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
