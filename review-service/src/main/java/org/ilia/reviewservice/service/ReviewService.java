package org.ilia.reviewservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.reviewservice.controller.request.CreateReviewDto;
import org.ilia.reviewservice.controller.request.UpdateReviewDto;
import org.ilia.reviewservice.entity.Review;
import org.ilia.reviewservice.enums.Role;
import org.ilia.reviewservice.feign.AppointmentClient;
import org.ilia.reviewservice.mapper.ReviewMapper;
import org.ilia.reviewservice.repository.ReviewRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
public class ReviewService {

    ReviewRepository reviewRepository;
    ReviewMapper reviewMapper;
    AppointmentClient appointmentClient;

    public List<Review> findAll(UUID doctorId) {
        return reviewRepository.findByDoctorId(doctorId);
    }

    public Review create(CreateReviewDto createReviewDto, UUID doctorId) {
        boolean patientWasOnAppointment = appointmentClient.find(Role.PATIENT, getCurrentUserId()).stream()
                .anyMatch(appointment -> appointment.getIsPatientCome() != null && appointment.getIsPatientCome());
        if (!patientWasOnAppointment) {
            throw new RuntimeException();
        }

        Review reviewToSave = reviewMapper.toReview(createReviewDto);
        reviewToSave.setPatientId(getCurrentUserId());
        reviewToSave.setDoctorId(doctorId);
        return reviewRepository.save(reviewToSave);
    }

    public Review update(UUID reviewId, UpdateReviewDto updateReviewDto, UUID doctorId) {
        return reviewRepository.findById(reviewId)
                .filter(review -> review.getDoctorId().equals(doctorId))
                .filter(review -> review.getPatientId().equals(getCurrentUserId()))
                .map(review -> reviewMapper.updateReview(updateReviewDto, review))
                .orElseThrow();
    }

    public void delete(UUID reviewId, UUID doctorId) {
        reviewRepository.findById(reviewId)
                .filter(review -> review.getDoctorId().equals(doctorId))
                .ifPresent(review -> reviewRepository.deleteById(reviewId));
    }

    private UUID getCurrentUserId() {
        BearerTokenAuthentication authentication = (BearerTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(((DefaultOAuth2AuthenticatedPrincipal) authentication.getPrincipal()).getName());
    }
}
