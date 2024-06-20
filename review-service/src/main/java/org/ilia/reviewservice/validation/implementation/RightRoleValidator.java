package org.ilia.reviewservice.validation.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ilia.reviewservice.enums.Role;
import org.ilia.reviewservice.validation.annotation.RightRole;

import static org.ilia.reviewservice.enums.Role.DOCTOR;

public class RightRoleValidator implements ConstraintValidator<RightRole, Role> {

    @Override
    public boolean isValid(Role role, ConstraintValidatorContext constraintValidatorContext) {
        return role == DOCTOR;
    }
}
