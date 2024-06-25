package org.ilia.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.userservice.controller.request.CreateUserDto;
import org.ilia.userservice.controller.request.LoginDto;
import org.ilia.userservice.controller.request.UpdateUserDto;
import org.ilia.userservice.controller.response.SuccessLoginDto;
import org.ilia.userservice.controller.response.UserDto;
import org.ilia.userservice.enums.Role;
import org.ilia.userservice.service.UserService;
import org.ilia.userservice.validation.annotation.RightRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.userservice.enums.Role.DOCTOR;
import static org.ilia.userservice.enums.Role.PATIENT;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/{role}")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@PathVariable @RightRole(allowedRoles = {DOCTOR, PATIENT}) Role role,
                                          @RequestBody CreateUserDto createUserDto) {
        return ResponseEntity.status(CREATED).body(userService.create(createUserDto, role));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable @RightRole(allowedRoles = {DOCTOR, PATIENT}) Role role,
                                          @PathVariable UUID userId,
                                          @RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok().body(userService.update(updateUserDto, role, userId));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessLoginDto> login(@PathVariable Role role,
                                                 @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok().body(userService.login(loginDto, role));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findById(@PathVariable Role role,
                                            @PathVariable UUID userId) {
        return ResponseEntity.ok().body(userService.findById(userId, role));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findByRole(@PathVariable @RightRole(allowedRoles = {DOCTOR, PATIENT}) Role role) {
        return ResponseEntity.ok().body(userService.findByRole(role));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable @RightRole(allowedRoles = {DOCTOR, PATIENT}) Role role,
                                    @PathVariable UUID userId) {
        userService.delete(userId, role);
        return ResponseEntity.ok().build();
    }
}
