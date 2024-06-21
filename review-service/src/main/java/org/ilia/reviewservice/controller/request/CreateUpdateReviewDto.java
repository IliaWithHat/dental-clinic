package org.ilia.reviewservice.controller.request;

import lombok.Value;

@Value
public class CreateUpdateReviewDto {

    Short mark;
    String title;
    String review;
}
