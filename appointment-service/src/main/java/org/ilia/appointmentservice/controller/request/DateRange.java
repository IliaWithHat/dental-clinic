package org.ilia.appointmentservice.controller.request;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class DateRange {

    LocalDate from;
    LocalDate to;
}
