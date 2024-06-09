package org.ilia.appointmentservice.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.appointmentservice.controller.request.CreateAppointmentRequest;
import org.ilia.appointmentservice.controller.request.DateRange;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentRequest;
import org.ilia.appointmentservice.controller.response.FindAppointmentResponse;
import org.ilia.appointmentservice.entity.Appointment;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.enums.State;
import org.ilia.appointmentservice.service.AppointmentService;
import org.ilia.appointmentservice.validation.annotation.RightRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/{role}/{userId}/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody CreateAppointmentRequest createAppointmentRequest,
                                              @PathVariable @RightRole Role role,
                                              @PathVariable UUID userId) {
        return ResponseEntity.status(CREATED).body(appointmentService.create(createAppointmentRequest, role, userId));
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<Appointment> update(@RequestBody UpdateAppointmentRequest updateAppointmentRequest,
                                              @PathVariable UUID appointmentId,
                                              @PathVariable @RightRole Role role,
                                              @PathVariable UUID userId) {
        return ResponseEntity.ok().body(appointmentService.update(updateAppointmentRequest, appointmentId, role, userId));
    }

    @GetMapping
    public ResponseEntity<List<FindAppointmentResponse>> find(@RequestParam(required = false) DateRange dateRange,
                                                              @RequestParam(required = false, defaultValue = "occupied") State state,
                                                              @PathVariable @RightRole Role role,
                                                              @PathVariable UUID userId) {
        return ResponseEntity.ok().body(appointmentService.find(dateRange, state, role, userId));
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> delete(@PathVariable UUID appointmentId,
                                    @PathVariable @RightRole Role role,
                                    @PathVariable UUID userId) {
        appointmentService.delete(appointmentId, role, userId);
        return ResponseEntity.ok().build();
    }
}
