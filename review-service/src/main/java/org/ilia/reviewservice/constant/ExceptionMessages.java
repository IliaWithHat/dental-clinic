package org.ilia.reviewservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessages {

    public static final String USER_NOT_FOUND_BY_ID_AND_ROLE = "User not found by id: %s and role: %s";
    public static final String REVIEW_NOT_FOUND = "Review not found by id: ";
    public static final String PATIENT_NOT_WAS_ON_APPOINTMENT = "Patient not was on appointment";
}
