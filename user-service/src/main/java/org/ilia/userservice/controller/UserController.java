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
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/{role}")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateUserDto createUserDto,
                                          @PathVariable @RightRole Role role) {
        return ResponseEntity.status(CREATED).body(userService.create(createUserDto, role));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable Role role, @PathVariable UUID userId,
                                          @RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok().body(userService.update(updateUserDto, role, userId));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessLoginDto> login(@RequestBody LoginDto loginDto,
                                                 @PathVariable Role role) {
        return ResponseEntity.ok().body(userService.login(loginDto, role));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findById(@PathVariable UUID userId,
                                            @PathVariable @RightRole Role role) {
        return ResponseEntity.ok().body(userService.findById(userId, role));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findByRole(@PathVariable @RightRole Role role) {
        return ResponseEntity.ok().body(userService.findByRole(role));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable UUID userId,
                                    @PathVariable @RightRole Role role) {
        userService.delete(userId, role);
        return ResponseEntity.ok().build();
    }
}
