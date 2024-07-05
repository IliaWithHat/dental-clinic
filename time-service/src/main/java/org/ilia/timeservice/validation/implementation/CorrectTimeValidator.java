package org.ilia.timeservice.validation.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ilia.timeservice.controller.request.CreateWorkingTimeDto;
import org.ilia.timeservice.enums.BreakTime;
import org.ilia.timeservice.validation.annotation.CorrectTime;

import java.time.temporal.ChronoField;

public class CorrectTimeValidator implements ConstraintValidator<CorrectTime, CreateWorkingTimeDto> {

    @Override
    public boolean isValid(CreateWorkingTimeDto dto, ConstraintValidatorContext context) {
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            return false;
        }
        if (dto.getBreakStartTime() == null && dto.getBreakEndTime() == null &&
            dto.getStartTime().isBefore(dto.getEndTime())) {
            return checkCorrectInterval(dto, BreakTime.NOT_EXIST);
        }
        if (dto.getBreakStartTime() != null && dto.getBreakEndTime() != null &&
            dto.getBreakStartTime().isBefore(dto.getBreakEndTime()) &&
            dto.getStartTime().isBefore(dto.getBreakStartTime()) &&
            dto.getEndTime().isAfter(dto.getBreakEndTime())) {
            return checkCorrectInterval(dto, BreakTime.EXIST);
        }
        return false;
    }

    private boolean checkCorrectInterval(CreateWorkingTimeDto dto, BreakTime breakTime) {
        long timeInterval = dto.getTimeIntervalInMinutes();

        if (breakTime == BreakTime.NOT_EXIST) {
            long totalMinutes = dto.getEndTime().getLong(ChronoField.MINUTE_OF_DAY) -
                                dto.getStartTime().getLong(ChronoField.MINUTE_OF_DAY);

            return totalMinutes % timeInterval == 0;
        } else {
            long beforeBreakMinutes = dto.getBreakStartTime().getLong(ChronoField.MINUTE_OF_DAY) -
                                      dto.getStartTime().getLong(ChronoField.MINUTE_OF_DAY);
            long afterBreakMinutes = dto.getEndTime().getLong(ChronoField.MINUTE_OF_DAY) -
                                     dto.getBreakEndTime().getLong(ChronoField.MINUTE_OF_DAY);

            return beforeBreakMinutes % dto.getTimeIntervalInMinutes() == 0 &&
                   afterBreakMinutes % dto.getTimeIntervalInMinutes() == 0;
        }
    }
}
