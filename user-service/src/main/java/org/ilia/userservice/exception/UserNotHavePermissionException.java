package org.ilia.userservice.exception;

public class UserNotHavePermissionException extends RuntimeException {

    public UserNotHavePermissionException(String message) {
        super(message);
    }
}
