package org.ilia.appointmentservice.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.appointmentservice.controller.request.CreateAppointmentRequest;
import org.ilia.appointmentservice.controller.request.DateRange;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentRequest;
import org.ilia.appointmentservice.entity.Appointment;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.enums.State;
import org.ilia.appointmentservice.validation.annotation.RightRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/{role}/{userId}/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    @PostMapping
    public ResponseEntity<Appointment> create(@PathVariable @RightRole Role role,
                                              @PathVariable String userId,
                                              @RequestBody CreateAppointmentRequest appointment) {
        return null;
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<Appointment> update(@PathVariable @RightRole Role role,
                                              @PathVariable String userId,
                                              @PathVariable String appointmentId,
                                              @RequestBody UpdateAppointmentRequest appointment) {
        return null;
    }

    @GetMapping
    public ResponseEntity<Appointment> find(@PathVariable @RightRole Role role,
                                            @PathVariable String userId,
                                            @RequestParam(required = false) DateRange dateRange,
                                            @RequestParam(required = false) State state) {
        return null;
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Appointment> delete(@PathVariable @RightRole Role role,
                                              @PathVariable String userId,
                                              @PathVariable String appointmentId) {
        return null;
    }
}
