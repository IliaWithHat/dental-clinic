package org.ilia.timeservice.validation.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ilia.timeservice.controller.request.CreateWorkingTimeDto;
import org.ilia.timeservice.validation.annotation.CorrectTime;

public class CorrectTimeValidator implements ConstraintValidator<CorrectTime, CreateWorkingTimeDto> {

    @Override
    public boolean isValid(CreateWorkingTimeDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getStartTime() == null || dto.getEndTime() == null) {
            return false;
        }
        if (dto.getBreakStartTime() == null && dto.getBreakEndTime() == null &&
            dto.getStartTime().isBefore(dto.getEndTime())) {
            return true;
        }
        if (dto.getBreakStartTime() != null && dto.getBreakEndTime() != null &&
            dto.getBreakStartTime().isBefore(dto.getBreakEndTime()) &&
            dto.getStartTime().isBefore(dto.getBreakStartTime()) &&
            dto.getEndTime().isAfter(dto.getBreakEndTime())) {
            return true;
        }
        return false;
    }
}
