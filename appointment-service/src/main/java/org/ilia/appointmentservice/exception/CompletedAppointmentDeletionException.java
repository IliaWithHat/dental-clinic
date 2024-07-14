package org.ilia.appointmentservice.exception;

public class CompletedAppointmentDeletionException extends RuntimeException {

    public CompletedAppointmentDeletionException(String message) {
        super(message);
    }
}
