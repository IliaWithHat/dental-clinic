package org.ilia.timeservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ilia.timeservice.validation.implementation.CorrectTimeValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CorrectTimeValidator.class)
public @interface CorrectTime {

    String message() default "incorrect time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
