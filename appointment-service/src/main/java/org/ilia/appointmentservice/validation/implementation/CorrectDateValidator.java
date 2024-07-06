package org.ilia.appointmentservice.validation.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ilia.appointmentservice.controller.request.DateRangeDto;
import org.ilia.appointmentservice.validation.annotation.CorrectDate;

public class CorrectDateValidator implements ConstraintValidator<CorrectDate, DateRangeDto> {

    @Override
    public boolean isValid(DateRangeDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getFrom() == null || dto.getTo() == null) {
            return false;
        }
        return dto.getFrom().isBefore(dto.getTo());
    }
}
