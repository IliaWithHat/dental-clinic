package org.ilia.reviewservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.reviewservice.controller.request.CreateUpdateReviewDto;
import org.ilia.reviewservice.controller.response.ReviewDto;
import org.ilia.reviewservice.entity.Review;
import org.ilia.reviewservice.enums.Role;
import org.ilia.reviewservice.exception.ReviewNotFoundException;
import org.ilia.reviewservice.exception.UserNotFoundException;
import org.ilia.reviewservice.feign.AppointmentServiceClient;
import org.ilia.reviewservice.feign.UserServiceClient;
import org.ilia.reviewservice.feign.response.UserDto;
import org.ilia.reviewservice.mapper.ReviewMapper;
import org.ilia.reviewservice.repository.ReviewRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.reviewservice.constant.ExceptionMessages.REVIEW_NOT_FOUND;
import static org.ilia.reviewservice.constant.ExceptionMessages.USER_NOT_FOUND_BY_ID_AND_ROLE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
public class ReviewService {

    ReviewRepository reviewRepository;
    ReviewMapper reviewMapper;
    AppointmentServiceClient appointmentServiceClient;
    UserServiceClient userServiceClient;

    public List<ReviewDto> findAll(Role role, UUID doctorId) {
        verifyUserExistByRoleAndId(role, doctorId);

        return reviewRepository.findByDoctorId(doctorId).stream()
                .map(reviewMapper::toReviewDto)
                .toList();
    }

    public ReviewDto create(Role role, UUID doctorId, CreateUpdateReviewDto createUpdateReviewDto) {
        verifyUserExistByRoleAndId(role, doctorId);

        boolean patientWasOnAppointment = appointmentServiceClient.find(Role.PATIENT, getCurrentUserId()).stream()
                .anyMatch(appointment -> appointment.getIsPatientCome() != null && appointment.getIsPatientCome());
        if (!patientWasOnAppointment) {
            throw new RuntimeException();
        }

        Review reviewToSave = reviewMapper.toReview(createUpdateReviewDto);
        reviewToSave.setPatientId(getCurrentUserId());
        reviewToSave.setDoctorId(doctorId);
        return reviewMapper.toReviewDto(reviewRepository.save(reviewToSave));
    }

    public ReviewDto update(Role role, UUID doctorId, UUID reviewId, CreateUpdateReviewDto createUpdateReviewDto) {
        verifyUserExistByRoleAndId(role, doctorId);

        return reviewRepository.findById(reviewId)
                .filter(review -> review.getDoctorId().equals(doctorId))
                .filter(review -> review.getPatientId().equals(getCurrentUserId()))
                .map(review -> reviewMapper.updateReview(createUpdateReviewDto, review))
                .map(reviewRepository::save)
                .map(reviewMapper::toReviewDto)
                .orElseThrow();
    }

    public void delete(Role role, UUID doctorId, UUID reviewId) {
        verifyUserExistByRoleAndId(role, doctorId);
        verifyReviewExistByIdAndDoctorId(reviewId, doctorId);

        reviewRepository.deleteById(reviewId);
    }

    private void verifyReviewExistByIdAndDoctorId(UUID reviewId, UUID doctorId) {
        if (reviewRepository.findByIdAndDoctorId(reviewId, doctorId).isEmpty()) {
            throw new ReviewNotFoundException(REVIEW_NOT_FOUND + reviewId);
        }
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(((DefaultOAuth2AuthenticatedPrincipal) authentication.getPrincipal()).getName());
    }

    private void verifyUserExistByRoleAndId(Role role, UUID id) {
        UserDto user = userServiceClient.findById(role, id);
        if (user.getRole() != role) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_BY_ID_AND_ROLE, id, role));
        }
    }
}
