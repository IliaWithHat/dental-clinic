package org.ilia.appointmentservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;

@Getter
@RequiredArgsConstructor
public class InvalidDateRangeException extends RuntimeException {

    private final BindingResult bindingResult;
}
