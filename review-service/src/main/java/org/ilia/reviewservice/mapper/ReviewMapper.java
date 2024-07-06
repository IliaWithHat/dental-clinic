package org.ilia.reviewservice.mapper;

import org.ilia.reviewservice.controller.request.CreateUpdateReviewDto;
import org.ilia.reviewservice.controller.response.ReviewDto;
import org.ilia.reviewservice.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface ReviewMapper {

    Review toReview(CreateUpdateReviewDto createUpdateReviewDto);

    Review updateReview(CreateUpdateReviewDto createUpdateReviewDto, @MappingTarget Review review);

    ReviewDto toReviewDto(Review review);
}
