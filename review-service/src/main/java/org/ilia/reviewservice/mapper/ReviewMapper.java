package org.ilia.reviewservice.mapper;

import org.ilia.reviewservice.controller.request.CreateReviewDto;
import org.ilia.reviewservice.controller.request.UpdateReviewDto;
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
    Review toReview(CreateReviewDto createReviewDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "patientId", ignore = true)
    @Mapping(target = "doctorId", ignore = true)
    Review updateReview(UpdateReviewDto updateReviewDto, @MappingTarget Review review);
}
