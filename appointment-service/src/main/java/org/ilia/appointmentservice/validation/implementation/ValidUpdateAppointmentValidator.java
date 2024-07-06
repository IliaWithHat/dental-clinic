package org.ilia.appointmentservice.validation.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ilia.appointmentservice.controller.request.UpdateAppointmentDto;
import org.ilia.appointmentservice.validation.annotation.ValidUpdateAppointment;

public class ValidUpdateAppointmentValidator implements ConstraintValidator<ValidUpdateAppointment, UpdateAppointmentDto> {

    @Override
    public boolean isValid(UpdateAppointmentDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getIsPatientCome() == null) {
            return false;
        }

        if (dto.getIsPatientCome()) {
            return dto.getServiceInfo() != null && !dto.getServiceInfo().isBlank() && dto.getPrice() != null;
        } else {
            return dto.getServiceInfo() == null && dto.getPrice() == null;
        }
    }
}
