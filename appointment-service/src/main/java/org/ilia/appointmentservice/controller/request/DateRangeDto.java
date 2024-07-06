package org.ilia.appointmentservice.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.ilia.appointmentservice.validation.annotation.CorrectDate;
import org.ilia.appointmentservice.validation.annotation.LimitDateRange;

import java.time.LocalDate;

@Value
@CorrectDate
@LimitDateRange
public class DateRangeDto {

    @NotNull(message = "from must not be null")
    LocalDate from;

    @NotNull(message = "to must not be null")
    LocalDate to;
}
