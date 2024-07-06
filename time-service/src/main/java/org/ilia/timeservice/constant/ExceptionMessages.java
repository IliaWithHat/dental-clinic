package org.ilia.timeservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessages {

    public static final String USER_NOT_FOUND_BY_ID_AND_ROLE = "User not found by id: %s and role: %s";
    public static final String WORKING_TIME_ALREADY_EXIST = "Working time already exist for doctor: ";
    public static final String WORKING_TIME_NOT_FOUND = "Working time not found for doctor: ";
    public static final String DUPLICATE_DAY = "Duplicate days are not allowed";
}
