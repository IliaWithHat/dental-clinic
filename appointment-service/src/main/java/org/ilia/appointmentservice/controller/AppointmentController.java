package org.ilia.appointmentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.appointmentservice.constant.HttpStatuses;
import org.ilia.appointmentservice.controller.request.CreateAppointmentDto;
import org.ilia.appointmentservice.controller.request.DateRangeDto;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentDto;
import org.ilia.appointmentservice.controller.response.AppointmentDto;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.enums.State;
import org.ilia.appointmentservice.exception.InvalidDateRangeException;
import org.ilia.appointmentservice.service.AppointmentService;
import org.ilia.appointmentservice.validation.annotation.RightRole;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.appointmentservice.constant.ExceptionMessages.STATE_FOR_ROLE_NOT_ALLOWED;
import static org.ilia.appointmentservice.enums.Role.DOCTOR;
import static org.ilia.appointmentservice.enums.Role.PATIENT;
import static org.ilia.appointmentservice.enums.State.FREE;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/{role}/{userId}/appointment")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Tag(name = "Appointment Management", description = "APIs for managing appointments")
public class AppointmentController {

    AppointmentService appointmentService;

    @Operation(summary = "Create a new appointment", description = "Creates a new appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "409", description = HttpStatuses.CONFLICT)
    })
    @PostMapping
    public ResponseEntity<AppointmentDto> create(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                                 @PathVariable UUID userId,
                                                 @RequestBody @Validated CreateAppointmentDto createAppointmentDto) {
        return ResponseEntity.status(CREATED).body(appointmentService.create(role, userId, createAppointmentDto));
    }

    @Operation(summary = "Update an appointment", description = "Updates an existing appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDto> update(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                                 @PathVariable UUID userId,
                                                 @PathVariable UUID appointmentId,
                                                 @RequestBody @Validated UpdateAppointmentDto updateAppointmentDto) {
        return ResponseEntity.ok().body(appointmentService.update(role, userId, appointmentId, updateAppointmentDto));
    }

    @Operation(summary = "Get appointment by ID", description = "Retrieves an appointment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDto> findById(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                                   @PathVariable UUID userId,
                                                   @PathVariable UUID appointmentId) {
        return ResponseEntity.ok().body(appointmentService.findById(role, userId, appointmentId));
    }

    @Operation(summary = "Find appointments", description = "Retrieves a list of appointments based on role, date range, and state")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "422", description = HttpStatuses.UNPROCESSABLE_ENTITY)
    })
    @GetMapping
    public ResponseEntity<List<AppointmentDto>> find(@PathVariable @RightRole(allowedRoles = {DOCTOR, PATIENT}) Role role,
                                                     @PathVariable UUID userId,
                                                     @ModelAttribute @Validated DateRangeDto dateRangeDto,
                                                     BindingResult dateRangeDtoBindingResult,
                                                     @RequestParam(required = false, defaultValue = "occupied") State state) {
        if (role == PATIENT && state == FREE) {
            throw new UnsupportedOperationException(STATE_FOR_ROLE_NOT_ALLOWED);
        }
        if (role == DOCTOR && dateRangeDtoBindingResult.hasErrors()) {
            throw new InvalidDateRangeException(dateRangeDtoBindingResult);
        }
        return ResponseEntity.ok().body(appointmentService.find(role, userId, dateRangeDto, state));
    }

    @Operation(summary = "Delete an appointment", description = "Deletes an appointment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> delete(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                    @PathVariable UUID userId,
                                    @PathVariable UUID appointmentId) {
        appointmentService.delete(role, userId, appointmentId);
        return ResponseEntity.ok().build();
    }
}
