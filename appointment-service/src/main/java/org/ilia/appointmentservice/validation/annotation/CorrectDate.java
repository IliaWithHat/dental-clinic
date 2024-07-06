package org.ilia.appointmentservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ilia.appointmentservice.validation.implementation.CorrectDateValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = CorrectDateValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface CorrectDate {

    String message() default "from must be before to";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
