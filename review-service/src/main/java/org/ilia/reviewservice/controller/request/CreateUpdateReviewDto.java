package org.ilia.reviewservice.controller.request;

import jakarta.validation.constraints.*;
import lombok.Value;

@Value
public class CreateUpdateReviewDto {

    @NotNull(message = "mark must not be null")
    @Min(value = 0, message = "mark must be greater than or equal to {value}")
    @Max(value = 5, message = "mark must be less than or equal to {value}")
    Short mark;

    @NotBlank(message = "title must not be blank")
    @Size(min = 1, max = 64, message = "title length must be between {min} and {max}")
    String title;

    @NotBlank(message = "review must not be blank")
    @Size(min = 1, max = 256, message = "review length must be between {min} and {max}")
    String review;
}
