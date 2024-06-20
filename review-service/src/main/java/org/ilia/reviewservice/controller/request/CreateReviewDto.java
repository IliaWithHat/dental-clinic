package org.ilia.reviewservice.controller.request;

import lombok.Value;

@Value
public class CreateReviewDto {

    Short mark;
    String title;
    String review;
}
