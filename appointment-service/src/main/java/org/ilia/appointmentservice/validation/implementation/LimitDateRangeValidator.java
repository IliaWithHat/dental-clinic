package org.ilia.appointmentservice.validation.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ilia.appointmentservice.controller.request.DateRangeDto;
import org.ilia.appointmentservice.validation.annotation.LimitDateRange;

import java.time.temporal.ChronoField;

public class LimitDateRangeValidator implements ConstraintValidator<LimitDateRange, DateRangeDto> {

    @Override
    public boolean isValid(DateRangeDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getFrom() == null || dto.getTo() == null) {
            return false;
        }
        return dto.getTo().getLong(ChronoField.EPOCH_DAY) - dto.getFrom().getLong(ChronoField.EPOCH_DAY) < 100;
    }
}
