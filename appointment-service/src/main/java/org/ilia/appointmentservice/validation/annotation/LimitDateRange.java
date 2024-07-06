package org.ilia.appointmentservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ilia.appointmentservice.validation.implementation.LimitDateRangeValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = LimitDateRangeValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface LimitDateRange {

    String message() default "range between from and to must me less than 3 months";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
