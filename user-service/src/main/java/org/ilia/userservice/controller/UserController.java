package org.ilia.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.userservice.controller.request.CreateUserRequest;
import org.ilia.userservice.controller.request.LoginRequest;
import org.ilia.userservice.controller.request.UpdateUserRequest;
import org.ilia.userservice.controller.response.LoginResponse;
import org.ilia.userservice.entity.User;
import org.ilia.userservice.enums.Role;
import org.ilia.userservice.service.UserService;
import org.ilia.userservice.validation.annotation.RightRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/{role}")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> create(@RequestBody CreateUserRequest createUserRequest,
                                       @PathVariable @RightRole Role role) {
        return ResponseEntity.status(CREATED).body(userService.create(createUserRequest, role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@RequestBody UpdateUserRequest updateUserRequest,
                                       @PathVariable @RightRole Role role) {
        return ResponseEntity.ok().body(userService.update(updateUserRequest, role));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                               @PathVariable @RightRole Role role) {
        return ResponseEntity.ok().body(userService.login(loginRequest, role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable UUID id,
                                         @PathVariable @RightRole Role role) {
        return ResponseEntity.ok().body(userService.findById(id, role));
    }

    @GetMapping
    public ResponseEntity<List<User>> findByRole(@PathVariable @RightRole Role role) {
        return ResponseEntity.ok().body(userService.findByRole(role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id,
                                    @PathVariable @RightRole Role role) {
        userService.delete(id, role);
        return ResponseEntity.ok().build();
    }
}
