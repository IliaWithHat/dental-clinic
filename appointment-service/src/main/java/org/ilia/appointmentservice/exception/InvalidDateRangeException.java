package org.ilia.appointmentservice.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class InvalidDateRangeException extends RuntimeException {

    private final BindingResult bindingResult;

    public InvalidDateRangeException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }
}
