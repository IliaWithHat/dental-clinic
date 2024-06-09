package org.ilia.appointmentservice.controller.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRange {

    private LocalDate from;
    private LocalDate to;
}
