package org.ilia.timeservice.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.timeservice.controller.request.CreateWorkingTimeRequest;
import org.ilia.timeservice.entity.WorkingTime;
import org.ilia.timeservice.service.WorkingTimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/working-time")
@RequiredArgsConstructor
public class WorkingTimeController {

    private final WorkingTimeService workingTimeService;

    @GetMapping("/{doctorId}")
    public ResponseEntity<List<WorkingTime>> findByDoctorId(@PathVariable UUID doctorId) {
        return ResponseEntity.ok().body(workingTimeService.findByDoctorId(doctorId));
    }

    @PostMapping
    public ResponseEntity<List<WorkingTime>> create(@RequestBody List<CreateWorkingTimeRequest> createWorkingTimeRequests) {
        return ResponseEntity.status(CREATED).body(workingTimeService.create(createWorkingTimeRequests));
    }

    @DeleteMapping("/{doctorId}")
    public ResponseEntity<?> delete(@PathVariable UUID doctorId) {
        workingTimeService.deleteByDoctorId(doctorId);
        return ResponseEntity.ok().build();
    }
}
