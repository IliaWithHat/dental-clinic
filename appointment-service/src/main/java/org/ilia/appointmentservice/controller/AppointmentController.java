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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/{role}/{userId}/appointment")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AppointmentController {

    AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentDto> create(@RequestBody CreateAppointmentDto createAppointmentDto,
                                                 @PathVariable @RightRole Role role,
                                                 @PathVariable UUID userId) {
        return ResponseEntity.status(CREATED).body(appointmentService.create(createAppointmentDto, role, userId));
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDto> update(@RequestBody UpdateAppointmentDto updateAppointmentDto,
                                                 @PathVariable UUID appointmentId,
                                                 @PathVariable @RightRole Role role,
                                                 @PathVariable UUID userId) {
        return ResponseEntity.ok().body(appointmentService.update(updateAppointmentDto, appointmentId, role, userId));
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDto> get(@PathVariable UUID appointmentId,
                                              @PathVariable @RightRole Role role,
                                              @PathVariable UUID userId) {
        return ResponseEntity.ok().body(appointmentService.findById(appointmentId, role, userId));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> find(@ModelAttribute DateRangeDto dateRangeDto,
                                                     @RequestParam(required = false, defaultValue = "occupied") State state,
                                                     @PathVariable @RightRole Role role,
                                                     @PathVariable UUID userId) {
        return ResponseEntity.ok().body(appointmentService.find(dateRangeDto, state, role, userId));
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> delete(@PathVariable UUID appointmentId,
                                    @PathVariable @RightRole Role role,
                                    @PathVariable UUID userId) {
        appointmentService.delete(appointmentId, role, userId);
        return ResponseEntity.ok().build();
    }
}
