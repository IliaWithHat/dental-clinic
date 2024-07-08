package org.ilia.appointmentservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessages {

    public static final String USER_NOT_FOUND_BY_ID_AND_ROLE = "User not found by id: %s and role: %s";
    public static final String DOCTOR_NOT_WORKING = "Doctor not working";
    public static final String APPOINTMENT_ALREADY_EXIST = "Appointment for this date already exist";
    public static final String APPOINTMENT_NOT_FOUND = "Appointment not found by id: ";
    public static final String STATE_FOR_ROLE_NOT_ALLOWED = "This state is not allowed for this role";
    public static final String INVALID_APPOINTMENT_DATE = "Invalid appointment date";
}
