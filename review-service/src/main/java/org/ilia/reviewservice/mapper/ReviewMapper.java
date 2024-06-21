package org.ilia.reviewservice.mapper;

import org.ilia.reviewservice.controller.request.CreateUpdateReviewDto;
import org.ilia.reviewservice.controller.response.ReviewDto;
import org.ilia.reviewservice.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "patientId", ignore = true)
    @Mapping(target = "doctorId", ignore = true)
    Review toReview(CreateUpdateReviewDto createUpdateReviewDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "patientId", ignore = true)
    @Mapping(target = "doctorId", ignore = true)
    Review updateReview(CreateUpdateReviewDto createUpdateReviewDto, @MappingTarget Review review);

    ReviewDto toReviewDto(Review review);
}
