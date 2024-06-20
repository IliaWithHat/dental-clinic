package org.ilia.reviewservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ilia.reviewservice.validation.implementation.RightRoleValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RightRoleValidator.class)
public @interface RightRole {

    String message() default "Role can only be 'doctor'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
