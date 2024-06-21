package org.ilia.mailschedulerservice.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class DateRange {

    LocalDate from;
    LocalDate to;
}
