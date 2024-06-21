package org.ilia.timeservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.timeservice.controller.request.CreateWorkingTimeDto;
import org.ilia.timeservice.controller.response.WorkingTimeDto;
import org.ilia.timeservice.service.WorkingTimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/working-time")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class WorkingTimeController {

    WorkingTimeService workingTimeService;

    @GetMapping("/{doctorId}")
    public ResponseEntity<List<WorkingTimeDto>> findByDoctorId(@PathVariable UUID doctorId) {
        return ResponseEntity.ok().body(workingTimeService.findByDoctorId(doctorId));
    }

    @PostMapping
    public ResponseEntity<List<WorkingTimeDto>> create(@RequestBody List<CreateWorkingTimeDto> createWorkingTimeDtos) {
        return ResponseEntity.status(CREATED).body(workingTimeService.create(createWorkingTimeDtos));
    }

    @DeleteMapping("/{doctorId}")
    public ResponseEntity<?> delete(@PathVariable UUID doctorId) {
        workingTimeService.deleteByDoctorId(doctorId);
        return ResponseEntity.ok().build();
    }
}
