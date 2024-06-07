package org.ilia.timeservice.repository;

import org.ilia.timeservice.entity.WorkingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkingTimeRepository extends JpaRepository<WorkingTime, Integer> {
}
