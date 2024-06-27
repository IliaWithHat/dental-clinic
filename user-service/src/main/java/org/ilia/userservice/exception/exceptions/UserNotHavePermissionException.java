package org.ilia.userservice.exception.exceptions;

public class UserNotHavePermissionException extends RuntimeException {

    public UserNotHavePermissionException(String message) {
        super(message);
    }
}
