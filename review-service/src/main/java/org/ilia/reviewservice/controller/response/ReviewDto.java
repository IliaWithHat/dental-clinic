package org.ilia.reviewservice.controller.response;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class ReviewDto {

    UUID id;
    Short mark;
    String title;
    String review;
    LocalDateTime date;
    UUID patientId;
    UUID doctorId;
}
