package org.ilia.timeservice.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.timeservice.entity.WorkingTime;
import org.ilia.timeservice.service.WorkingTimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/working-time")
@RequiredArgsConstructor
public class WorkingTimeController {

    private final WorkingTimeService workingTimeService;

    @GetMapping("/{doctorId}")
    public ResponseEntity<List<WorkingTime>> findByDoctorId(@PathVariable Long doctorId) {
        return null;
    }

    @PostMapping
    public ResponseEntity<List<WorkingTime>> create(@RequestBody List<WorkingTime> workingTimes) {
        return null;
    }

    @DeleteMapping("/{doctorId}")
    public ResponseEntity<?> delete(@PathVariable Long doctorId) {
        return null;
    }
}
