package org.ilia.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.userservice.constant.HttpStatuses;
import org.ilia.userservice.controller.request.CreateUserDto;
import org.ilia.userservice.controller.request.LoginDto;
import org.ilia.userservice.controller.request.UpdateUserDto;
import org.ilia.userservice.controller.response.SuccessLoginDto;
import org.ilia.userservice.controller.response.UserDto;
import org.ilia.userservice.enums.Role;
import org.ilia.userservice.exception.InvalidIsWorkingFieldException;
import org.ilia.userservice.service.UserService;
import org.ilia.userservice.validation.annotation.RightRole;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.userservice.constant.ExceptionMessages.INVALID_IS_WORKING_FIELD;
import static org.ilia.userservice.enums.Role.DOCTOR;
import static org.ilia.userservice.enums.Role.PATIENT;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/{role}")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    UserService userService;

    @Operation(summary = "Create a user", description = "Creates a new user (doctor or patient)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "409", description = HttpStatuses.CONFLICT)
    })
    @PostMapping
    public ResponseEntity<UserDto> create(@PathVariable @RightRole(allowedRoles = {DOCTOR, PATIENT}) Role role,
                                          @RequestBody @Validated CreateUserDto createUserDto) {
        validateIsWorkingField(role, createUserDto.getIsWorking());
        return ResponseEntity.status(CREATED).body(userService.create(role, createUserDto));
    }

    @Operation(summary = "Update a user", description = "Updates an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable @RightRole(allowedRoles = {DOCTOR, PATIENT}) Role role,
                                          @PathVariable UUID userId,
                                          @RequestBody UpdateUserDto updateUserDto) {
        validateIsWorkingField(role, updateUserDto.getIsWorking());
        return ResponseEntity.ok().body(userService.update(role, userId, updateUserDto));
    }

    private void validateIsWorkingField(Role role, Boolean isWorkingField) {
        if (role == DOCTOR && isWorkingField == null || role == PATIENT && isWorkingField != null) {
            throw new InvalidIsWorkingFieldException(INVALID_IS_WORKING_FIELD + role);
        }
    }

    @Operation(summary = "User login", description = "Authenticates a user and returns login information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = SuccessLoginDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/login")
    public ResponseEntity<SuccessLoginDto> login(@PathVariable Role role,
                                                 @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok().body(userService.login(role, loginDto));
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findById(@PathVariable Role role,
                                            @PathVariable UUID userId) {
        return ResponseEntity.ok().body(userService.findById(role, userId));
    }

    @Operation(summary = "Find users by role", description = "Retrieves a list of users by their role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping
    public ResponseEntity<List<UserDto>> findByRole(@PathVariable @RightRole(allowedRoles = {DOCTOR, PATIENT}) Role role) {
        return ResponseEntity.ok().body(userService.findByRole(role));
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable @RightRole(allowedRoles = {DOCTOR, PATIENT}) Role role,
                                    @PathVariable UUID userId) {
        userService.delete(role, userId);
        return ResponseEntity.ok().build();
    }
}
