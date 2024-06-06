package org.ilia.userservice.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email) {
        super("User with this email not found: " + email);
    }
}
