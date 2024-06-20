package org.ilia.reviewservice.controller.request;

import lombok.Value;

@Value
public class UpdateReviewDto {

    Short mark;
    String title;
    String review;
}
