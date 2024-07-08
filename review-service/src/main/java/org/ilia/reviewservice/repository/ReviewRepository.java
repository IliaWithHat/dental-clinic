package org.ilia.reviewservice.repository;

import org.ilia.reviewservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Optional<Review> findByIdAndDoctorId(UUID id, UUID doctorId);

    List<Review> findByDoctorId(UUID doctorId);
}
