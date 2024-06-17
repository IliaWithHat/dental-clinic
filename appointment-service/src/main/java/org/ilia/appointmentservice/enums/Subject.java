package org.ilia.appointmentservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum Subject {
    APPOINTMENT_CONFIRMATION("Appointment Confirmation", "appointment-confirmation"),
    APPOINTMENT_REMINDER("Reminder: Upcoming Appointment", "appointment-reminder"),
    WELCOME("Welcome to Dental Clinic!", "welcome");

    String emailSubject;
    String templateName;
}
