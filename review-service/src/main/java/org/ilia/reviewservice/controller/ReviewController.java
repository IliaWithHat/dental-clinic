package org.ilia.reviewservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.reviewservice.controller.request.CreateReviewDto;
import org.ilia.reviewservice.controller.request.UpdateReviewDto;
import org.ilia.reviewservice.entity.Review;
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
    public ResponseEntity<List<Review>> findAll(@PathVariable @RightRole Role role, @PathVariable UUID doctorId) {
        return ResponseEntity.ok().body(reviewService.findAll(doctorId));
    }

    @PostMapping
    public ResponseEntity<Review> create(@PathVariable @RightRole Role role, @PathVariable UUID doctorId,
                                         @RequestBody CreateReviewDto createReviewDto) {
        return ResponseEntity.status(CREATED).body(reviewService.create(createReviewDto, doctorId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> update(@PathVariable @RightRole Role role, @PathVariable UUID doctorId,
                                         @PathVariable UUID reviewId, @RequestBody UpdateReviewDto updateReviewDto) {
        return ResponseEntity.ok().body(reviewService.update(reviewId, updateReviewDto, doctorId));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> delete(@PathVariable @RightRole Role role, @PathVariable UUID doctorId,
                                    @PathVariable UUID reviewId) {
        reviewService.delete(reviewId, doctorId);
        return ResponseEntity.ok().build();
    }
}
