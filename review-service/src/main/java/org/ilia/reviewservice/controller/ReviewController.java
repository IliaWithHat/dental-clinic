package org.ilia.reviewservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.reviewservice.constant.HttpStatuses;
import org.ilia.reviewservice.controller.request.CreateUpdateReviewDto;
import org.ilia.reviewservice.controller.response.ReviewDto;
import org.ilia.reviewservice.enums.Role;
import org.ilia.reviewservice.service.ReviewService;
import org.ilia.reviewservice.validation.annotation.RightRole;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.reviewservice.enums.Role.DOCTOR;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/{role}/{doctorId}/review")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Tag(name = "Review Management", description = "APIs for managing reviews")
public class ReviewController {

    ReviewService reviewService;

    @Operation(summary = "Get all reviews", description = "Retrieves all reviews for a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = ReviewDto.class))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping
    public ResponseEntity<List<ReviewDto>> findAll(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                                   @PathVariable UUID doctorId) {
        return ResponseEntity.ok().body(reviewService.findAll(role, doctorId));
    }

    @Operation(summary = "Create a review", description = "Creates a new review for a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = ReviewDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping
    public ResponseEntity<ReviewDto> create(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                            @PathVariable UUID doctorId,
                                            @RequestBody @Validated CreateUpdateReviewDto createUpdateReviewDto) {
        return ResponseEntity.status(CREATED).body(reviewService.create(role, doctorId, createUpdateReviewDto));
    }

    @Operation(summary = "Update a review", description = "Updates an existing review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = ReviewDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> update(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                            @PathVariable UUID doctorId,
                                            @PathVariable UUID reviewId,
                                            @RequestBody @Validated CreateUpdateReviewDto createUpdateReviewDto) {
        return ResponseEntity.ok().body(reviewService.update(role, doctorId, reviewId, createUpdateReviewDto));
    }

    @Operation(summary = "Delete a review", description = "Deletes a review by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> delete(@PathVariable @RightRole(allowedRoles = DOCTOR) Role role,
                                    @PathVariable UUID doctorId,
                                    @PathVariable UUID reviewId) {
        reviewService.delete(role, doctorId, reviewId);
        return ResponseEntity.ok().build();
    }
}
