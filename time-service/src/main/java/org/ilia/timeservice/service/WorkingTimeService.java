package org.ilia.timeservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.timeservice.controller.request.CreateWorkingTimeRequest;
import org.ilia.timeservice.entity.WorkingTime;
import org.ilia.timeservice.mapper.WorkingTimeMapper;
import org.ilia.timeservice.repository.WorkingTimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
public class WorkingTimeService {

    WorkingTimeRepository workingTimeRepository;
    WorkingTimeMapper workingTimeMapper;

    public List<WorkingTime> findByDoctorId(UUID doctorId) {
        return workingTimeRepository.findByDoctorId(doctorId);
    }

    public List<WorkingTime> create(List<CreateWorkingTimeRequest> createWorkingTimeRequests) {
        return createWorkingTimeRequests.stream()
                .map(workingTimeMapper::toWorkingTime)
                .peek(workingTimeRepository::save)
                .toList();
    }

    public void deleteByDoctorId(UUID doctorId) {
        workingTimeRepository.deleteByDoctorId(doctorId);
    }
}
