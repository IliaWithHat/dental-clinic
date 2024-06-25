package org.ilia.appointmentservice.validation.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.experimental.FieldDefaults;
import org.ilia.appointmentservice.enums.Role;
import org.ilia.appointmentservice.validation.annotation.RightRole;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
public class RightRoleValidator implements ConstraintValidator<RightRole, Role> {

    List<Role> allowedRoles;

    @Override
    public void initialize(RightRole constraintAnnotation) {
        allowedRoles = List.of(constraintAnnotation.allowedRoles());
    }

    @Override
    public boolean isValid(Role role, ConstraintValidatorContext constraintValidatorContext) {
        return allowedRoles.contains(role);
    }
}
