package org.ilia.reviewservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.reviewservice.controller.request.CreateUpdateReviewDto;
import org.ilia.reviewservice.controller.response.ReviewDto;
import org.ilia.reviewservice.enums.Role;
import org.ilia.reviewservice.service.ReviewService;
import org.ilia.reviewservice.validation.annotation.RightRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequestMapping("/v1/{role}/{doctorId}/review")
public class ReviewController {

    ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewDto>> findAll(@PathVariable @RightRole Role role, @PathVariable UUID doctorId) {
        return ResponseEntity.ok().body(reviewService.findAll(doctorId));
    }

    @PostMapping
    public ResponseEntity<ReviewDto> create(@PathVariable @RightRole Role role, @PathVariable UUID doctorId,
                                            @RequestBody CreateUpdateReviewDto createUpdateReviewDto) {
        return ResponseEntity.status(CREATED).body(reviewService.create(createUpdateReviewDto, doctorId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> update(@PathVariable @RightRole Role role, @PathVariable UUID doctorId,
                                            @PathVariable UUID reviewId, @RequestBody CreateUpdateReviewDto createUpdateReviewDto) {
        return ResponseEntity.ok().body(reviewService.update(reviewId, createUpdateReviewDto, doctorId));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> delete(@PathVariable @RightRole Role role, @PathVariable UUID doctorId,
                                    @PathVariable UUID reviewId) {
        reviewService.delete(reviewId, doctorId);
        return ResponseEntity.ok().build();
    }
}
