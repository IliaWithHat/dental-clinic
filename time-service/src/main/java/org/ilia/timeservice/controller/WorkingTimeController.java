package org.ilia.timeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.timeservice.constant.HttpStatuses;
import org.ilia.timeservice.controller.request.CreateWorkingTimeDto;
import org.ilia.timeservice.controller.response.WorkingTimeDto;
import org.ilia.timeservice.enums.Role;
import org.ilia.timeservice.exception.DuplicateDayException;
import org.ilia.timeservice.service.WorkingTimeService;
import org.ilia.timeservice.validation.annotation.RightRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.timeservice.constant.ExceptionMessages.DUPLICATE_DAY;
import static org.ilia.timeservice.enums.Role.DOCTOR;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/{role}/{doctorId}/working-time")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Tag(name = "Working Time Management", description = "APIs for managing working times")
public class WorkingTimeController {

    WorkingTimeService workingTimeService;

    @Operation(summary = "Get working times", description = "Retrieves all working times for a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = WorkingTimeDto.class))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping
    public ResponseEntity<List<WorkingTimeDto>> findByDoctorId(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                                               @PathVariable UUID doctorId) {
        return ResponseEntity.ok().body(workingTimeService.findByDoctorId(role, doctorId));
    }

    @Operation(summary = "Create working times", description = "Creates new working times for a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = WorkingTimeDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "409", description = HttpStatuses.CONFLICT)
    })
    @PostMapping
    public ResponseEntity<List<WorkingTimeDto>> create(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                                       @PathVariable UUID doctorId,
                                                       @RequestBody @NotEmpty List<@Valid CreateWorkingTimeDto> createWorkingTimeDtoList) {
        validateWorkingTime(createWorkingTimeDtoList);
        return ResponseEntity.status(CREATED).body(workingTimeService.create(role, doctorId, createWorkingTimeDtoList));
    }

    private void validateWorkingTime(List<CreateWorkingTimeDto> createWorkingTimeDtoList) {
        Set<DayOfWeek> uniqueDays = new HashSet<>();
        for (CreateWorkingTimeDto dto : createWorkingTimeDtoList) {
            if (!uniqueDays.add(dto.getDay())) {
                throw new DuplicateDayException(DUPLICATE_DAY);
            }
        }
    }

    @Operation(summary = "Delete working times", description = "Deletes all working times for a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                    @PathVariable UUID doctorId) {
        workingTimeService.deleteByDoctorId(role, doctorId);
        return ResponseEntity.ok().build();
    }
}
