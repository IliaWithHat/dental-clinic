package org.ilia.appointmentservice.validation.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.validation.annotation.RightRole;

import static org.ilia.appointmentservice.enums.Role.DOCTOR;
import static org.ilia.appointmentservice.enums.Role.PATIENT;

public class RightRoleValidator implements ConstraintValidator<RightRole, Role> {

    @Override
    public boolean isValid(Role role, ConstraintValidatorContext constraintValidatorContext) {
        return role == PATIENT || role == DOCTOR;
    }
}
