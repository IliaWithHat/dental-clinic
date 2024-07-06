package org.ilia.appointmentservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.appointmentservice.controller.request.CreateAppointmentDto;
import org.ilia.appointmentservice.controller.request.DateRangeDto;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentDto;
import org.ilia.appointmentservice.controller.response.AppointmentDto;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.enums.State;
import org.ilia.appointmentservice.service.AppointmentService;
import org.ilia.appointmentservice.validation.annotation.RightRole;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/v1/{role}/{userId}/appointment")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AppointmentController {

    AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentDto> create(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                                 @PathVariable UUID userId,
                                                 @RequestBody @Validated CreateAppointmentDto createAppointmentDto) {
        return ResponseEntity.status(CREATED).body(appointmentService.create(role, userId, createAppointmentDto));
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDto> update(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                                 @PathVariable UUID userId,
                                                 @PathVariable UUID appointmentId,
                                                 @RequestBody @Validated UpdateAppointmentDto updateAppointmentDto) {
        return ResponseEntity.ok().body(appointmentService.update(role, userId, appointmentId, updateAppointmentDto));
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDto> findById(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                                   @PathVariable UUID userId,
                                                   @PathVariable UUID appointmentId) {
        return ResponseEntity.ok().body(appointmentService.findById(role, userId, appointmentId));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> find(@PathVariable @RightRole(allowedRoles = {DOCTOR, PATIENT}) Role role,
                                                     @PathVariable UUID userId,
                                                     @ModelAttribute @Validated DateRangeDto dateRangeDto,
                                                     @RequestParam(required = false, defaultValue = "occupied") State state) {
        if (role == PATIENT && state == FREE) {
            throw new UnsupportedOperationException(STATE_FOR_ROLE_NOT_ALLOWED);
        }
        return ResponseEntity.ok().body(appointmentService.find(role, userId, dateRangeDto, state));
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> delete(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                    @PathVariable UUID userId,
                                    @PathVariable UUID appointmentId) {
        appointmentService.delete(role, userId, appointmentId);
        return ResponseEntity.ok().build();
    }
}
