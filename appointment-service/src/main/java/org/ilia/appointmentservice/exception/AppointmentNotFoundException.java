package org.ilia.appointmentservice.exception;

public class AppointmentNotFoundException extends RuntimeException {

    public AppointmentNotFoundException(String messages) {
        super(messages);
    }
}
