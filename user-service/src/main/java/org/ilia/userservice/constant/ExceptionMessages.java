package org.ilia.userservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessages {

    public static final String USER_NOT_HAVE_PERMISSION = "You don't have the right permission";
    public static final String USER_ALREADY_EXISTS = "User already exists by email: ";
    public static final String USER_NOT_FOUND_BY_EMAIL_AND_ROLE = "User not found by email: %s and role: %s";
    public static final String USER_NOT_FOUND_BY_ID_AND_ROLE = "User not found by id: %s and role: %s";
    public static final String INVALID_IS_WORKING_FIELD = "Invalid param field for role: ";
}
