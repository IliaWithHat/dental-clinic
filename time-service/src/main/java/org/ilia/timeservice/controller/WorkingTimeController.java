package org.ilia.timeservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.timeservice.controller.request.CreateWorkingTimeDto;
import org.ilia.timeservice.controller.response.WorkingTimeDto;
import org.ilia.timeservice.enums.Role;
import org.ilia.timeservice.service.WorkingTimeService;
import org.ilia.timeservice.validation.annotation.RightRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.timeservice.enums.Role.DOCTOR;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/{role}/{doctorId}/working-time")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class WorkingTimeController {

    WorkingTimeService workingTimeService;

    @GetMapping
    public ResponseEntity<List<WorkingTimeDto>> findByDoctorId(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                                               @PathVariable UUID doctorId) {
        return ResponseEntity.ok().body(workingTimeService.findByDoctorId(role, doctorId));
    }

    @PostMapping
    public ResponseEntity<List<WorkingTimeDto>> create(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                                       @PathVariable UUID doctorId,
                                                       @RequestBody List<CreateWorkingTimeDto> createWorkingTimeDtoList) {
        return ResponseEntity.status(CREATED).body(workingTimeService.create(role, doctorId, createWorkingTimeDtoList));
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                    @PathVariable UUID doctorId) {
        workingTimeService.deleteByDoctorId(role, doctorId);
        return ResponseEntity.ok().build();
    }
}
