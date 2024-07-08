package org.ilia.reviewservice.exception;

public class PatientNotWasOnAppointmentException extends RuntimeException {

    public PatientNotWasOnAppointmentException(String message) {
        super(message);
    }
}
