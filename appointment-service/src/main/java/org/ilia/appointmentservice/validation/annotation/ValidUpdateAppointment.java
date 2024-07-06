package org.ilia.appointmentservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ilia.appointmentservice.validation.implementation.ValidUpdateAppointmentValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ValidUpdateAppointmentValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface ValidUpdateAppointment {

    String message() default "invalid data";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}