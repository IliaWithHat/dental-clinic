package org.ilia.appointmentservice.exception;

public class AppointmentAlreadyExistException extends RuntimeException {

    public AppointmentAlreadyExistException(String message) {
        super(message);
    }
}
