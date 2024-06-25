package org.ilia.appointmentservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.validation.implementation.RightRoleValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RightRoleValidator.class)
public @interface RightRole {

    String message() default "Incorrect 'role'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Role[] allowedRoles() default {};
}
