package org.ilia.appointmentservice.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.appointmentservice.controller.request.CreateAppointmentRequest;
import org.ilia.appointmentservice.controller.request.DateRange;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentRequest;
import org.ilia.appointmentservice.entity.Appointment;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.enums.State;
import org.ilia.appointmentservice.service.AppointmentService;
import org.ilia.appointmentservice.validation.annotation.RightRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/{role}/{userId}/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody CreateAppointmentRequest appointment,
                                              @PathVariable @RightRole Role role,
                                              @PathVariable String userId) {
        return ResponseEntity.status(CREATED).body(appointmentService.create(appointment, role, userId));
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<Appointment> update(@RequestBody UpdateAppointmentRequest appointment,
                                              @PathVariable String appointmentId,
                                              @PathVariable @RightRole Role role,
                                              @PathVariable String userId) {
        return ResponseEntity.ok().body(appointmentService.update(appointment, appointmentId, role, userId));
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> find(@RequestParam(required = false) DateRange dateRange,
                                                  @RequestParam(required = false) State state,
                                                  @PathVariable @RightRole Role role,
                                                  @PathVariable String userId) {
        return ResponseEntity.ok().body(appointmentService.find(dateRange, state, role, userId));
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> delete(@PathVariable String appointmentId,
                                    @PathVariable @RightRole Role role,
                                    @PathVariable String userId) {
        appointmentService.delete(appointmentId, role, userId);
        return ResponseEntity.ok().build();
    }
}
