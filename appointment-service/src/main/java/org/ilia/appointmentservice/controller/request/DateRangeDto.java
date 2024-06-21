package org.ilia.appointmentservice.controller.request;

import lombok.Value;

import java.time.LocalDate;

@Value
public class DateRangeDto {

    LocalDate from;
    LocalDate to;
}
