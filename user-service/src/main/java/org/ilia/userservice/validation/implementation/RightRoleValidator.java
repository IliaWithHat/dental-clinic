package org.ilia.userservice.validation.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ilia.userservice.enums.Role;
import org.ilia.userservice.validation.annotation.RightRole;

import static org.ilia.userservice.enums.Role.DOCTOR;
import static org.ilia.userservice.enums.Role.PATIENT;

public class RightRoleValidator implements ConstraintValidator<RightRole, Role> {

    @Override
    public boolean isValid(Role role, ConstraintValidatorContext constraintValidatorContext) {
        return role == PATIENT || role == DOCTOR;
    }
}
