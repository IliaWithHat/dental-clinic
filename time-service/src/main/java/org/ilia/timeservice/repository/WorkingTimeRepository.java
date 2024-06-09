package org.ilia.timeservice.repository;

import org.ilia.timeservice.entity.WorkingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkingTimeRepository extends JpaRepository<WorkingTime, UUID> {

    List<WorkingTime> findByDoctorId(UUID doctorId);

    void deleteByDoctorId(UUID doctorId);
}
