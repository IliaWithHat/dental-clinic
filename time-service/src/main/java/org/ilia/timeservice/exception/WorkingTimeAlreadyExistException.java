package org.ilia.timeservice.exception;

public class WorkingTimeAlreadyExistException extends RuntimeException {

    public WorkingTimeAlreadyExistException(String message) {
        super(message);
    }
}
